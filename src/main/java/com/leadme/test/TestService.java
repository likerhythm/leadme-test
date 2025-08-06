package com.leadme.test;

import com.leadme.test.entity.Content;
import com.leadme.test.entity.MetaInfo;
import com.leadme.test.entity.UserWeight;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {

    private final ContentRepository contentRepository;
    private final UserWeightRepository userWeightRepository;
    private final MetaInfoRepository metaInfoRepository;
    private final GenreVectorHolder genreVectorHolder;

    @Transactional
    public List<Double> processActionAndCalcVector(Long contentId) {
        // content 장르 조회
        List<MetaInfo> metaInfoByContentId = contentRepository.findMetaInfoByContentId(contentId);

        // 장르 가중치 업데이트
        double defaultWeight = 0.1;
        long userId = 1;
        for (MetaInfo metaInfo : metaInfoByContentId) {
            Long metaInfoId = metaInfo.getId();
            if (userWeightRepository.existsByUserIdAndMetaInfoId(userId, metaInfoId)) {
                UserWeight userWeight = userWeightRepository.findByUserIdAndMetaInfoId(userId, metaInfoId);
                userWeight.updateWeight(defaultWeight);
                continue;
            }
            userWeightRepository.save(UserWeight.builder()
                                                .userId(userId)
                                                .metaInfoId(metaInfoId)
                                                .weight(defaultWeight)
                                                .build());
        }

        // 벡터 계산
        List<UserWeight> userWeights = userWeightRepository.findByUserId(userId);

        List<Double> vectorSum = null;
        int vectorCount = userWeights.size();

        for (UserWeight uw : userWeights) {
            Long metaInfoId = uw.getMetaInfoId();
            Double weight = uw.getWeight();

            MetaInfo metaInfo = metaInfoRepository.findById(metaInfoId)
                    .orElseThrow(() -> new RuntimeException("잘못된 metaInfoId입니다."));
            String name = metaInfo.getName();
            String rawVector = genreVectorHolder.getVector(name);

            List<Double> weightedVector = stringToVector(rawVector, weight);

            if (vectorSum == null) {
                vectorSum = new ArrayList<>(weightedVector); // 첫 번째 벡터로 초기화
            } else {
                for (int i = 0; i < vectorSum.size(); i++) {
                    vectorSum.set(i, vectorSum.get(i) + weightedVector.get(i)); // 요소별 합산
                }
            }
        }

        if (vectorSum != null) {
            List<Double> userVector = vectorSum.stream()
                    .map(v -> v / vectorCount)
                    .toList();

            System.out.println("✅ 평균 벡터:");
            System.out.println(userVector);

            return userVector;
        }

        return null;
    }

    @Transactional
    public void processActionAndRecommend(Long contentId) {
        List<Double> userVector = processActionAndCalcVector(contentId);
        List<Content> allContents = contentRepository.findAll();

        // 콘텐츠와 유사도 쌍을 저장할 리스트
        List<Pair<Content, Double>> scoredContents = new ArrayList<>();

        for (Content content : allContents) {
            String embedding = content.getEmbedding();
            if (embedding == null || embedding.isBlank()) continue;

            List<Double> contentVector = Arrays.stream(embedding.split(","))
                    .map(Double::parseDouble)
                    .toList();

            double similarity = cosineSimilarity(userVector, contentVector);
            scoredContents.add(Pair.of(content, similarity));
        }

        // 유사도 기준 내림차순 정렬 후 상위 5개 추출
        List<Content> top5 = scoredContents.stream()
                .sorted((a, b) -> Double.compare(b.getSecond(), a.getSecond()))
                .limit(5)
                .map(Pair::getFirst)
                .toList();

        System.out.println("🔝 추천 콘텐츠:");
        top5.forEach(c -> System.out.println(" - contentId: " + c.getId()));
    }

    private List<Double> stringToVector(String rawVector, double weight) {
        return Arrays.stream(rawVector.split(","))
                .map(Double::parseDouble)
                .map(v -> v * weight)
                .toList();
    }

    private double cosineSimilarity(List<Double> a, List<Double> b) {
        if (a.size() != b.size()) throw new IllegalArgumentException("벡터 길이 불일치");

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.size(); i++) {
            double valA = a.get(i);
            double valB = b.get(i);
            dotProduct += valA * valB;
            normA += valA * valA;
            normB += valB * valB;
        }

        if (normA == 0 || normB == 0) return 0.0;

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

}
