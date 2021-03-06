### 1. C1: System Context (C4 diagram)

 ```
  
                 +-----------------+
                 |                 |
                 |  Source (.csv)  |
                 |                 |
                 +-----------------+
                          ^
                          |
                          |
             +--------------------------+         +-----------------+
             |                          |         |                 |
  [REST API] +  Simple Data Warehouse   + ------> +  Elasticsearch  |
    query    |      (ETL system)        + <------ +   (data store)  |
      &      |                          |         |                 |
   extract   +--------------------------+         +-----------------+
      
  ```

### 2. Rebuild with docker locally

```docker-compose build && docker-compose up```

### 3. Important links

* The demo application : https://simple-data-warehouse-dev.herokuapp.com

* Swagger (REST API documentation): https://simple-data-warehouse-dev.herokuapp.com/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

* The repository: https://github.com/maciejscislowski/simple-data-warehouse

#### 3.1. Example possible queries

* Total clicks: ```GET``` https://simple-data-warehouse-dev.herokuapp.com/api/v1/total-clicks?datasource=Google%20Ads&fromDaily=now-20M/M&toDaily=now&size=1

* Impressions: ```GET``` https://simple-data-warehouse-dev.herokuapp.com/api/v1/impressions?size=2

* CTR: ```GET``` https://simple-data-warehouse-dev.herokuapp.com/api/v1/ctr?datasource=Google%20Ads&campaign=Remarketing&size=1

* Generic query (with Elasticsearch query as the body): ```POST``` https://simple-data-warehouse-dev.herokuapp.com/api/v1/query

* Generic query with cURL ```curl -H "Content-Type: application/json" -d '{"query":{"bool":{"must":[{"term":{"datasource.keyword":"Google Ads"}},{"range":{"daily":{"gte":"now-20M/M","lte":"now"}}}]}},"aggs":{"group_by_datasources":{"terms":{"field":"datasource.keyword"},"aggs":{"clicks_per_datasource":{"sum":{"field":"clicks"}}}}},"size":1}' https://simple-data-warehouse-dev.herokuapp.com/api/v1/query```
