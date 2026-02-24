# Test Konfigürasyonu ve Kullanım Kılavuzu

## 📋 İçindekiler

1. [Backend Test Konfigürasyonu](#backend-test-konfigürasyonu)
2. [Frontend Test Konfigürasyonu](#frontend-test-konfigürasyonu)
3. [Local Database Setup](#local-database-setup)
4. [Test Çalıştırma](#test-çalıştırma)

## 🔧 Backend Test Konfigürasyonu

### Test Dependencies

Backend'de şu test kütüphaneleri kullanılıyor:

- **JUnit 5** - Test framework
- **Spring Boot Test** - Spring test utilities
- **TestContainers** - PostgreSQL container for integration tests
- **MockMvc** - Web layer testing
- **AssertJ** - Fluent assertions

### Test Yapısı

```
src/test/java/com/hss/hss_backend/
├── base/
│   └── BaseIntegrationTest.java      # Base class for integration tests
├── config/
│   └── TestDatabaseConfig.java       # TestContainers configuration
├── repository/
│   └── AnimalRepositoryTest.java     # Repository test örneği
├── service/
│   └── AnimalServiceTest.java        # Service test örneği
└── controller/
    └── AnimalControllerTest.java     # Controller test örneği
```

### Test Profile

Testler `application-test.yaml` profile'ını kullanır:
- TestContainers PostgreSQL otomatik başlatılır
- Cloud SQL devre dışı
- Flyway migration'ları çalışır

### Test Çalıştırma

```bash
# Tüm testleri çalıştır
./mvnw test

# Sadece unit testler
./mvnw test -Dtest=*Test

# Sadece integration testler
./mvnw test -Dtest=*IntegrationTest

# Belirli bir test sınıfı
./mvnw test -Dtest=AnimalRepositoryTest

# Test coverage raporu
./mvnw test jacoco:report
```

## 🎨 Frontend Test Konfigürasyonu

### Test Dependencies

Frontend'de şu test kütüphaneleri kullanılıyor:

- **Jest** - Test framework
- **React Testing Library** - Component testing
- **@testing-library/jest-dom** - DOM matchers

### Test Utilities

`src/test-utils/testUtils.tsx` dosyası tüm gerekli provider'ları içerir:
- ThemeProvider
- AuthProvider
- AppProvider
- ErrorProvider
- BrowserRouter

### Test Çalıştırma

```bash
# Tüm testleri çalıştır
npm test

# Watch mode
npm test -- --watch

# Coverage raporu
npm test -- --coverage

# Belirli bir test dosyası
npm test -- LoadingSpinner.test.tsx
```

## 🗄️ Local Database Setup

### Docker Compose ile PostgreSQL

1. **Docker Compose'u başlat:**
```bash
cd hssbackend
docker-compose up -d
```

2. **Database'in hazır olduğunu kontrol et:**
```bash
docker-compose ps
```

3. **Application'ı local profile ile çalıştır:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### Application Local Profile

`application-local.yaml` dosyası Docker Compose PostgreSQL'i kullanır:
- Host: `localhost:5432`
- Database: `hss_dev`
- User: `hss-dev-user`
- Password: `DevPassword123!`

### Test Data Seeding

`DataSeeder` component'i otomatik olarak test verileri ekler:
- Species ve Breeds
- Owners
- Animals
- Staff ve Roles

Seed işlemi sadece database boşsa çalışır.

### Database Bağlantısını Test Et

```bash
# PostgreSQL'e bağlan
docker exec -it hss-postgres-dev psql -U hss-dev-user -d hss_dev

# Tabloları listele
\dt

# Hayvanları listele
SELECT * FROM animal;
```

## 📝 Test Örnekleri

### Backend Repository Test

```java
@DisplayName("AnimalRepository Integration Tests")
class AnimalRepositoryTest extends BaseIntegrationTest {
    
    @Test
    void shouldSaveAndRetrieveAnimal() {
        // Test implementation
    }
}
```

### Backend Service Test

```java
@DisplayName("AnimalService Integration Tests")
class AnimalServiceTest extends BaseIntegrationTest {
    
    @Test
    void shouldCreateAnimal() {
        // Test implementation
    }
}
```

### Backend Controller Test

```java
@AutoConfigureMockMvc
@DisplayName("AnimalController Integration Tests")
class AnimalControllerTest extends BaseIntegrationTest {
    
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldCreateAnimalViaAPI() throws Exception {
        // Test implementation
    }
}
```

### Frontend Component Test

```typescript
import { render, screen } from '../test-utils/testUtils';
import LoadingSpinner from './LoadingSpinner';

describe('LoadingSpinner', () => {
  it('should render spinner when isLoading is true', () => {
    render(<LoadingSpinner isLoading={true} />);
    expect(screen.getByRole('progressbar')).toBeInTheDocument();
  });
});
```

## 🚀 Hızlı Başlangıç

1. **Backend testlerini çalıştır:**
```bash
cd hssbackend
./mvnw test
```

2. **Frontend testlerini çalıştır:**
```bash
cd HSS/HSS
npm test
```

3. **Local database'i başlat:**
```bash
cd hssbackend
docker-compose up -d
```

4. **Backend'i local profile ile çalıştır:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## 📊 Test Coverage

Test coverage raporlarını görüntülemek için:

**Backend:**
```bash
./mvnw test jacoco:report
# Rapor: target/site/jacoco/index.html
```

**Frontend:**
```bash
npm test -- --coverage
# Rapor: coverage/lcov-report/index.html
```

## 🔍 Troubleshooting

### TestContainers Docker Hatası

Eğer TestContainers çalışmıyorsa:
```bash
# Docker'ın çalıştığını kontrol et
docker ps

# TestContainers log'larını kontrol et
./mvnw test -X
```

### Database Connection Hatası

Local database'e bağlanamıyorsanız:
```bash
# Docker container'ın çalıştığını kontrol et
docker-compose ps

# Log'ları kontrol et
docker-compose logs postgres

# Container'ı yeniden başlat
docker-compose restart postgres
```

### Frontend Test Hatası

Frontend testleri çalışmıyorsa:
```bash
# Node modules'ü yeniden yükle
rm -rf node_modules package-lock.json
npm install

# Jest cache'ini temizle
npm test -- --clearCache
```

