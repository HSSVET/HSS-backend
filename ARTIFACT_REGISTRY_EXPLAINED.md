# Artifact Registry - Nedir ve Neden Gerekli?

## 🎯 Basit Açıklama

**Artifact Registry**, Docker image'larınızı saklayan ve dağıtan bir "kütüphane" gibidir.

### Normal Dünya Örneği
```
Kodu yazıyorsunuz (kitap yazmak gibi)
  ↓
Kitabı bastırıyorsunuz (build - Docker image oluşturur)
  ↓
Kitabı bir kütüphaneye koyuyorsunuz (Artifact Registry)
  ↓
Başkaları kitabı kütüphaneden alıp okuyabiliyor (deploy)
```

### Kod Dünyasında
```
Spring Boot kodu yazıyorsunuz
  ↓
Cloud Build Docker image oluşturur
  ↓
Image'ı Artifact Registry'ye push eder
  ↓
Cloud Run image'ı registry'den çekip deploy eder
```

## 🏗️ Mimari

### Geliştirici Dizini:
```
/
├── HSS/                 (Frontend - React)
│   └── npm run build → static files → Firebase Hosting
│
└── hss-backend/         (Backend - Spring Boot)
    └── mvnw package → JAR → Docker → Artifact Registry
```

### Cloud Build Akışı:

```
1. LOCAL DEPLOYMENT (Şu an)
   ┌─────────────────┐
   │  Geliştirici    │
   │  Kodu yazıyor   │
   └────────┬────────┘
            │ git push
            ↓
   ┌─────────────────┐
   │  GitHub/Repo     │
   └────────┬────────┘
            │ Trigger
            ↓
   ┌─────────────────┐     ┌─────────────────┐
   │  Cloud Build     │────→│  Artifact       │
   │  Image Build     │     │  Registry       │
   │  (Docker)        │     │  (Depolama)     │
   └────────┬────────┘     └────────┬────────┘
            │                        │
            │ Push                   │ Pull
            ↓                        ↓
   ┌─────────────────┐     ┌─────────────────┐
   │  Image Ready!   │────→│  Cloud Run      │
   │  (tar.gz + tag) │     │  Deploy         │
   └─────────────────┘     └─────────────────┘
```

## 📦 Artifact Registry İçeriği

### Her Image Tag'le Saklanır:

```
Artifact Registry: europe-west3-docker.pkg.dev/hss-cloud-473511/hss-backend

├── hss-backend:abc123  (commit SHA - sabit)
├── hss-backend:def456  (commit SHA)
├── hss-backend:latest  (her zaman en son)
└── hss-backend:v1.0.0  (version tag)
```

**Neden Farklı Tag'ler?**
- `abc123`: Spesifik bir deployment için (production'a gönderilecek olan)
- `latest`: Her zaman en son başarılı build
- `v1.0.0`: Release version

## 🔄 Workflow: Cloud Build → Artifact Registry → Cloud Run

### 1️⃣ Build Aşaması (Cloud Build)
```yaml
steps:
  - name: 'gcr.io/cloud-builders/docker'
    args:
      - 'build'
      - '-t'
      - 'europe-west3-docker.pkg.dev/hss-cloud-473511/hss-backend/hss-backend:abc123'
      - '-t'
      - 'europe-west3-docker.pkg.dev/hss-cloud-473511/hss-backend/hss-backend:latest'
```

**Ne Oluyor?**
```
Dockerfile + Kaynak Kod → Docker Build → Image oluşturuldu
```

### 2️⃣ Push Aşaması (Artifact Registry)
```yaml
  - name: 'gcr.io/cloud-builders/docker'
    args:
      - 'push'
      - '--all-tags'
      - 'europe-west3-docker.pkg.dev/hss-cloud-473511/hss-backend/hss-backend'
```

**Ne Oluyor?**
```
Image → Artifact Registry'ye gönderiliyor (Google Cloud Storage benzeri)
```

### 3️⃣ Deploy Aşaması (Cloud Run)
```yaml
  - name: 'gcr.io/cloud-builders/gcloud'
    args:
      - 'run'
      - 'deploy'
      - '--image=europe-west3-docker.pkg.dev/hss-cloud-473511/hss-backend/hss-backend:abc123'
```

**Ne Oluyor?**
```
Cloud Run → Registry'den image'ı çekiyor → Container başlatıyor
```

## 🔐 Güvenlik ve İzinler

### Artifact Registry'de Sahiplik:

```yaml
# Kim okuyabilir?
- Cloud Build: ✅ (yazabilir - push eder)
- Cloud Run: ✅ (okuyabilir - pull eder)
- Geliştirici: ✅ (yerel test için)
- Dışarıdan: ❌ (private - sadece proje içinde)
```

### IAM İzinleri Gerekli:

```bash
# Cloud Build service account'a yazma izni
roles/artifactregistry.writer

# Cloud Run service account'a okuma izni
roles/artifactregistry.reader (default olarak var)
```

## 💰 Maliyet

- **Storage**: İlk 500 MB ücretsiz, sonrası GB başına $0.10
- **Operations**: İlk 1000 operasyon ücretsiz
- **Minimum**: Çoğu küçük projede ücretsiz

**Örnek:**
```
1 image = ~200MB
10 tag = 2GB
Ayda 100 pull = ~$0.20 (neredeyse ücretsiz)
```

## 🆚 Alternatifler: Container Registry vs Artifact Registry

| Özellik | Container Registry (ESKİ) | Artifact Registry (YENİ) |
|---------|---------------------------|--------------------------|
| Docker support | ✅ | ✅ |
| Maven/NPM/Python | ❌ | ✅ |
| Multi-region | ❌ | ✅ |
| Vulnerability scanning | Limited | ✅ |
| **Tavsiye** | ❌ | ✅ **KULLAN** |

## 🎯 Neden Artifact Registry Kullanıyoruz?

### ❌ Artifact Registry OLMADAN:
```
Cloud Build image oluşturuyor → Nereye koysun?
→ Cloud Run image'ı nereden alsın?
→ Her build'de Docker image yeniden oluşturulmalı?
```

**Sorunlar:**
1. Her build'de Docker image sıfırdan build olmalı
2. Versioning zor
3. Rollback yok
4. Geçmiş build'ler kayboluyor

### ✅ Artifact Registry İLE:
```
Cloud Build image oluşturuyor → Registry'ye push
→ Cloud Run registry'den çekip deploy ediyor
→ Version takibi kolay
→ Rollback mümkün
```

**Avantajlar:**
1. ✅ Version control (git gibi)
2. ✅ Rollback yapabilirsiniz
3. ✅ Multi-environment (dev, staging, prod)
4. ✅ Build cache (daha hızlı build)
5. ✅ Security scanning

## 📊 Gerçek Hayat Örneği

### Senaryo: Production'a Deploy

```bash
# 1. Tag oluştur
git tag -a v1.2.0 -m "Release"

# 2. Push et
git push origin v1.2.0

# 3. Cloud Build başlar
#    - Image build: abc123
#    - Registry'ye push: hss-backend:abc123, hss-backend:v1.2.0
#    - Cloud Run deploy: hss-backend:abc123

# 4. Problem var!
#    - Servis çöktü

# 5. Rollback
gcloud run services update-traffic hss-backend \
  --to-revisions=previous:v1.1.0 \
  --region=europe-west3

# Artifact Registry sayesinde eski versiyon zaten orada!
```

### Artifact Registry'de Görünüm:

```
Repository: hss-backend

📦 Image: hss-backend
├── tag: abc123 (production - çöktü)
├── tag: def456 (v1.1.0 - previous)
├── tag: ghi789 (v1.0.0 - rollback olarak kullanılabilir)
└── tag: latest (her zaman son)
```

## 🛠️ Manuel Komutlar

### Image'i Pull (İndir)
```bash
docker pull europe-west3-docker.pkg.dev/hss-cloud-473511/hss-backend/hss-backend:latest
```

### Image'i Push (Yükle)
```bash
docker tag hss-backend:local europe-west3-docker.pkg.dev/hss-cloud-473511/hss-backend/hss-backend:v1.0.0
docker push europe-west3-docker.pkg.dev/hss-cloud-473511/hss-backend/hss-backend:v1.0.0
```

### Registry'deki Image'leri Listele
```bash
gcloud artifacts docker images list \
  europe-west3-docker.pkg.dev/hss-cloud-473511/hss-backend/hss-backend
```

## ✅ Özet

**Artifact Registry = Konteyner kütüphanesi**

1. ✅ **Build:** Kodu Docker image'a çevir
2. ✅ **Push:** Image'ı registry'ye kaydet
3. ✅ **Pull:** Deploy ederken registry'den çek
4. ✅ **Versiyon:** Her tag bir versiyon
5. ✅ **Güvenlik:** Private, sadece proje içi
6. ✅ **Hız:** Build cache sayesinde hızlı

**Cloud Build kullanmadan Artifact Registry olmaz!**
