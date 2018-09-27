package com.cosmos.cassandra;

import java.util.List;

public interface RecommendationRepository {

	List<Recommendation> getRecommendations();

}