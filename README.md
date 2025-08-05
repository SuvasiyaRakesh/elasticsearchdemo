# 📚 Elasticsearch Course Search App

This is a Spring Boot application that indexes course data into Elasticsearch and provides powerful search and autocomplete APIs.

---

## 🚀 Features

✅ Index 50+ course documents on startup  
✅ Full-text search on title and description  
✅ Filter by age, price, category, type, and date  
✅ Sort by price or next session date  
✅ Pagination support  
✅ Autocomplete suggestions using Elasticsearch Completion Suggester  
✅ Fuzzy search (typo-tolerant)

---

## 🧰 Tech Stack

- Java 17
- Spring Boot 3.2.4
- Spring Data Elasticsearch
- Elasticsearch 7.17.15
- Docker & Docker Compose
- Jackson for JSON parsing
- Lombok

---

## 🧪 Prerequisites

- Java 17
- Maven
- Docker & Docker Compose
- Git

---

## 🐳 Step 1: Run Elasticsearch using Docker

Create a `docker-compose.yml` file in the root of the project:

```yaml
version: '3.8'
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:7.17.15
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
      - ES_JAVA_OPTS=-Xms512m -Xmx512m
    ports:
      - 9200:9200
```

Run it:

```bash
docker-compose up -d
```

Check if it's running:

```bash
curl http://localhost:9200
```

---

## ⚙️ Step 2: Clone & Build the Project

```bash
git clone https://github.com/SuvasiyaRakesh/elasticsearchdemo.git
cd elasticsearchdemo
mvn clean install
```

---

## ▶️ Step 3: Run the Application

```bash
./mvnw spring-boot:run
```

Expected log:

```
✅ Indexed 50 courses into Elasticsearch
```

---

## 📁 Sample Data

Ensure the file `sample-courses.json` is in:  
```
src/main/resources/sample-courses.json
```

Format example:

```json
{
  "id": "course-001",
  "title": "Math Magic",
  "description": "A fun and interactive course on Math Magic",
  "category": "Math",
  "type": "online",
  "price": 805.56,
  "minAge": 7,
  "nextSessionDate": "2026-01-19T17:44:58.742Z",
  "suggest": {
    "input": ["Math Magic"]
  }
}
```

---

## 🔎 API Endpoints

### 1. **Search Courses**

`GET /api/search`

#### Query Parameters:

| Param         | Type    | Example                   |
|---------------|---------|---------------------------|
| q             | string  | `math`                    |
| minAge, maxAge| integer | `7`                       |
| minPrice, maxPrice | double | `500`               |
| category      | string  | `Math`, `Science`         |
| type          | string  | `online`, `offline`       |
| startDate     | ISO-8601| `2025-08-10T00:00:00Z`    |
| sort          | string  | `priceAsc`, `priceDesc`   |
| page          | int     | `0`                       |
| size          | int     | `10`                      |

#### Example:

```bash
curl "http://localhost:8081/api/search?q=magic&minAge=6&category=Math&sort=priceAsc"
```

---

### 2. **Autocomplete Suggestions**

`GET /api/search/suggest?q=mat`

#### Example:

```bash
curl "http://localhost:8081/api/search/suggest?q=mat"
```

---

## 🧪 Testing

- Ensure Elasticsearch is running
- Start the Spring Boot app
- Use Postman or browser to hit the endpoints
- Adjust `application.properties` if needed

---

## 📹 Video Demo (What to Record)

1. Show Elasticsearch running in Docker
2. Run the Spring Boot app and show console logs (`✅ Indexed X courses`)
3. Open Postman or browser and:
   - Call `/api/search?q=math`
   - Call `/api/search/suggest?q=mat`
4. Show pagination, sorting, and filters
5. (Optional) Show fuzzy match results

---

## 🧾 Submission Checklist

✅ GitHub repo: `https://github.com/SuvasiyaRakesh/elasticsearchdemo`  
✅ Video uploaded to Drive/YouTube (3–5 mins)  
✅ All sample data included  
✅ README with setup and examples (✅ This file)

---

## 👤 Author

**Rakesh Suvasiya**  
Spring Boot + Elasticsearch Developer  
[GitHub: SuvasiyaRakesh](https://github.com/SuvasiyaRakesh)

---
