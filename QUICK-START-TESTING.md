# 🚀 Test Konfigürasyonu - Hızlı Başlangıç

## 1️⃣ Local Database Kurulumu

### Docker Compose ile PostgreSQL Başlat

```bash
cd hssbackend
docker-compose up -d
```

### Database Bağlantısını Kontrol Et

```bash
# Container'ın çalıştığını kontrol et
docker-compose ps

# PostgreSQL'e bağlan
docker exec -it hss-postgres-dev psql -U hss-dev-user -d hss_dev
```

## 2️⃣ Backend Testleri

### Test Dependencies Kurulumu

TestContainers ve diğer test kütüphaneleri `pom.xml`'e eklendi. Maven otomatik olarak indirecek.

### Testleri Çalıştır

```bash
cd hssbackend

# Tüm testleri çalıştır
./mvnw test

# Sadece AnimalRepository testi
./mvnw test -Dtest=AnimalRepositoryTest

# Test coverage
./mvnw test jacoco:report
```

### Backend'i Local Database ile Çalıştır

```bash
# Local profile ile çalıştır (test data otomatik seed edilir)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## 3️⃣ Frontend Testleri

### Testleri Çalıştır

```bash
cd HSS/HSS

# Tüm testleri çalıştır
npm test

# Watch mode
npm test -- --watch

# Coverage
npm test -- --coverage
```

## 4️⃣ Test Yapısı

### Backend Test Yapısı

```
src/test/java/com/hss/hss_backend/
├── base/
│   └── BaseIntegrationTest.java      # Tüm integration testler için base class
├── config/
│   └── TestDatabaseConfig.java       # TestContainers PostgreSQL config
├── repository/
│   └── AnimalRepositoryTest.java     # Repository test örneği ✅
├── service/
│   └── AnimalServiceTest.java        # Service test örneği ✅
└── controller/
    └── AnimalControllerTest.java     # Controller test örneği ✅
```

### Frontend Test Yapısı

```
src/
├── test-utils/
│   └── testUtils.tsx                 # Test utilities ve providers ✅
├── components/
│   └── LoadingSpinner.test.tsx       # Component test örneği ✅
└── features/animals/components/
    └── AnimalList.test.tsx           # Feature test örneği ✅
```

## 5️⃣ Test Data Seeding

`DataSeeder` component'i otomatik olarak şunları ekler:
- ✅ Species (Kedi, Köpek, Kuş)
- ✅ Breeds (her species için)
- ✅ Owners (3 adet)
- ✅ Animals (her owner için)
- ✅ Staff (2 adet)
- ✅ Roles (ADMIN, VETERINARIAN, STAFF, RECEPTIONIST)

Seed işlemi sadece database boşsa çalışır.

## 6️⃣ Test Örnekleri

### Backend Repository Test

```java
@DisplayName("AnimalRepository Integration Tests")
class AnimalRepositoryTest extends BaseIntegrationTest {
    // TestContainers PostgreSQL otomatik başlar
    // @Transactional ile her test sonunda rollback
}
```

### Backend Service Test

```java
@DisplayName("AnimalService Integration Tests")
class AnimalServiceTest extends BaseIntegrationTest {
    // Gerçek database ile integration test
}
```

### Backend Controller Test

```java
@AutoConfigureMockMvc
@DisplayName("AnimalController Integration Tests")
class AnimalControllerTest extends BaseIntegrationTest {
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldCreateAnimalViaAPI() {
        // MockMvc ile API test
    }
}
```

### Frontend Component Test

```typescript
import { render, screen } from '../test-utils/testUtils';

describe('LoadingSpinner', () => {
  it('should render spinner', () => {
    render(<LoadingSpinner isLoading={true} />);
    expect(screen.getByRole('progressbar')).toBeInTheDocument();
  });
});
```

## 7️⃣ Troubleshooting

### TestContainers Çalışmıyor

```bash
# Docker'ın çalıştığını kontrol et
docker ps

# TestContainers için Docker socket erişimi gerekli
```

### Database Connection Hatası

```bash
# Docker container'ı kontrol et
docker-compose ps

# Log'ları görüntüle
docker-compose logs postgres

# Container'ı yeniden başlat
docker-compose restart postgres
```

### Frontend Test Hatası

```bash
# Cache temizle
npm test -- --clearCache

# Node modules yeniden yükle
rm -rf node_modules && npm install
```

## 📚 Daha Fazla Bilgi

Detaylı bilgi için `README-TESTING.md` dosyasına bakın.

