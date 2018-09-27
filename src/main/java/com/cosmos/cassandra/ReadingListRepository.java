package com.cosmos.cassandra;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;


public interface ReadingListRepository extends CassandraRepository<Book, UUID> {
	@AllowFiltering
	public List<Book> findByReader(String reader);
	}
