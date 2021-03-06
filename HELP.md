### C1: System Context (C4 diagram)

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

### Rebuild with docker locally

```docker-compose build && docker-compose up```

### Important links

* The demo application : https://simple-data-warehouse-dev.herokuapp.com

* Swagger (REST API documentation): https://simple-data-warehouse-dev.herokuapp.com/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

* The repository: https://github.com/maciejscislowski/simple-data-warehouse

#### Example possible queries

* Total clicks: ```GET``` https://simple-data-warehouse-dev.herokuapp.com/api/v1/total-clicks?datasource=Google%20Ads&fromDaily=now-20M/M&toDaily=now&size=1

* Impressions: ```GET``` https://simple-data-warehouse-dev.herokuapp.com/api/v1/impressions?size=2

* CTR: ```GET``` https://simple-data-warehouse-dev.herokuapp.com/api/v1/ctr?datasource=Google%20Ads&campaign=Remarketing&size=1

* Generic query (with Elasticsearch query as the body): ```GET``` https://simple-data-warehouse-dev.herokuapp.com/api/v1/query
