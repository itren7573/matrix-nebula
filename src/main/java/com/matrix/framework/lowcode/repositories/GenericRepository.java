package com.matrix.framework.lowcode.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用低代码仓库
 *
 * Copyright © 海平面工作室 版权所有
 *
 * @Author: Leo
 * @Create: 2024/1/30
 * @Since 1.0
 */
@Repository
public class GenericRepository {

    private final DatabaseClient databaseClient;

    public GenericRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<Map<String, Object>> save(String tableName, Map<String, Object> data) {
        Long id = data.get("id") != null ? Long.parseLong(data.get("id").toString()) : null;
        
        if (id == null) {
            return insert(tableName, data);
        } else {
            // 先获取原有数据
            return findById(tableName, id)
                .flatMap(existingData ->
                     update(tableName, id, data)
                );
        }
    }

    private Mono<Map<String, Object>> insert(String tableName, Map<String, Object> data) {
        String columns = String.join(", ", data.keySet());
        String placeholders = data.keySet().stream()
                .map(k -> ":" + k)
                .collect(Collectors.joining(", "));

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s) RETURNING id", 
                tableName, columns, placeholders);

        return databaseClient.sql(sql)
                .bindValues(data)
                .fetch()
                .first()
                .map(result -> {
                    data.put("id", result.get("id"));
                    return data;
                });
    }

    private Mono<Map<String, Object>> update(String tableName, Long id, Map<String, Object> data) {
        // 只处理data中实际存在且不为null的字段
        String setClause = data.keySet().stream()
                .filter(k -> !k.equals("id") && data.get(k) != null)  // 排除id字段
                .filter(data::containsKey)     // 确保字段在data中存在
                .map(k -> k + " = :" + k)
                .collect(Collectors.joining(", "));

        // 如果没有需要更新的字段，直接返回原数据
        if (setClause.isEmpty()) {
            return Mono.just(data);
        }

        String sql = String.format("UPDATE %s SET %s WHERE id = :id",
                tableName, setClause);

        // 创建一个新的Map，只包含要更新的字段
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("id", id);
        data.forEach((k, v) -> {
            if (!k.equals("id") && data.containsKey(k) && data.get(k) != null) {
                updateData.put(k, v);
            }
        });

        return databaseClient.sql(sql)
                .bindValues(updateData)
                .fetch()
                .rowsUpdated()
                .then(Mono.just(data));
    }

    public Mono<Void> delete(String tableName, Long id) {
        String sql = String.format("DELETE FROM %s WHERE id = :id", tableName);
        
        return databaseClient.sql(sql)
                .bind("id", id)
                .fetch()
                .rowsUpdated()
                .then();
    }

    public Mono<Map<String, Object>> findAll(String tableName, PageRequest pageable, String search) {
        StringBuilder baseQuery = new StringBuilder("SELECT * FROM " + tableName);
        StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) as total FROM " + tableName);
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isEmpty()) {
            return getTableColumns(tableName)
                .flatMap(columns -> {
                    baseQuery.append(" WHERE ");
                    countQuery.append(" WHERE ");
                    String searchConditions = columns.stream()
                            .map(column -> column + " LIKE ?")
                            .collect(Collectors.joining(" OR "));
                    baseQuery.append(searchConditions);
                    countQuery.append(searchConditions);
                    columns.forEach(column -> params.add("%" + search + "%"));

                    if (pageable != null) {
                        baseQuery.append(" LIMIT ").append(pageable.getPageSize())
                                .append(" OFFSET ").append(pageable.getOffset());
                    }

                    DatabaseClient.GenericExecuteSpec baseSpec = databaseClient.sql(baseQuery.toString());
                    DatabaseClient.GenericExecuteSpec countSpec = databaseClient.sql(countQuery.toString());

                    for (int i = 0; i < params.size(); i++) {
                        final int index = i;
                        baseSpec = baseSpec.bind(index, params.get(index));
                        countSpec = countSpec.bind(index, params.get(index));
                    }

                    return Mono.zip(
                            baseSpec.fetch().all().collectList(),
                            countSpec.fetch().one().map(row -> row.get("total"))
                    ).map(tuple -> {
                        Map<String, Object> result = new HashMap<>();
                        result.put("items", tuple.getT1());
                        result.put("total", tuple.getT2());
                        return result;
                    });
                });
        } else {
            if (pageable != null) {
                baseQuery.append(" LIMIT ").append(pageable.getPageSize())
                        .append(" OFFSET ").append(pageable.getOffset());
            }

            DatabaseClient.GenericExecuteSpec baseSpec = databaseClient.sql(baseQuery.toString());
            DatabaseClient.GenericExecuteSpec countSpec = databaseClient.sql(countQuery.toString());

            return Mono.zip(
                    baseSpec.fetch().all().collectList(),
                    countSpec.fetch().one().map(row -> row.get("total"))
            ).map(tuple -> {
                Map<String, Object> result = new HashMap<>();
                result.put("items", tuple.getT1());
                result.put("total", tuple.getT2());
                return result;
            });
        }
    }

    public Mono<Map<String, Object>> findById(String tableName, Long id) {
        String sql = String.format("SELECT * FROM %s WHERE id = :id", tableName);
        
        return databaseClient.sql(sql)
                .bind("id", id)
                .fetch()
                .first();
    }

    private Mono<List<String>> getTableColumns(String tableName) {
        String sql = "SELECT column_name FROM information_schema.columns " +
                "WHERE table_name = ?";
        
        return databaseClient.sql(sql)
                .bind(0, tableName)
                .fetch()
                .all()
                .map(row -> (String) row.get("column_name"))
                .collectList();
    }
} 