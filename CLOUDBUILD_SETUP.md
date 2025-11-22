# Cloud Build Kurulumu - HSS Backend

## 📋 Özet

Bu dosya backend'iniz için Cloud Build CI/CD pipeline'ını kurmanız için gereken tüm adımları içerir.

## 🚀 Hızlı Kurulum (Otomatik Script)

```bash
cd hss-backend
chmod +x setup-cloud-build.sh
./setup-cloud-build.sh
```

## 🔧 Manuel Kurulum (Adım Adım)

### 1️⃣ Artifact Registry Repository Oluştur

```bash
gcloud artifacts repositories create hss-backend \
  --repository-format=docker \
  --location=europe-west3 \
  --project=hss-cloud-473511 \
  --description="Docker images for HSS Backend Service"
```

### 2️⃣ Secret Manager'da Secrets Oluştur

#### a) Database Username
```bash
echo "hss-user" | gcloud secrets create db-username \
  --data-file=- \
  --replication-policy="automatic" \
  --project=hss-cloud-473511
```

#### b) Database Password
```bash
echo "YOUR_SECURE_PASSWORD" | gcloud secrets create db-password \
  --data-file=- \
  --replication-policy="automatic" \
  --project=hss-cloud-473511
```

#### c) Firebase Service Account (JSON dosyasınız varsa)
```bash
gcloud secrets create firebase-service-account \
  --data-file=path/to/service-account.json \
  --replication-policy="automatic" \
  --project=hss-cloud-473511
```

**Not:** Eğer secret'lar zaten varsa, yeni version ekleyin:
```bash
echo "new-value" | gcloud secrets versions add db-username --data-file=-
```

### 3️⃣ Cloud Build için IAM İzinleri

Cloud Build service account'una gerekli izinleri verin:

```bash
PROJECT_ID="hss-cloud-473511"
PROJECT_NUMBER=$(gcloud projects describe ${PROJECT_ID} --format="value(projectNumber)")
CLOUDBUILD_SA="${PROJECT_NUMBER}@cloudbuild.gserviceaccount.com"

# Cloud Run Admin
gcloud projects add-iam-policy-binding ${PROJECT_ID} \
  --member="serviceAccount:${CLOUDBUILD_SA}" \
  --role="roles/run.admin"

# Service Account User
gcloud projects add-iam-policy-binding ${PROJECT_ID} \
  --member="serviceAccount:${CLOUDBUILD_SA}" \
  --role="roles/iam.serviceAccountUser"

# Artifact Registry Writer
gcloud projects add-iam-policy-binding ${PROJECT_ID} \
  --member="serviceAccount:${CLOUDBUILD_SA}" \
  --role="roles/artifactregistry.writer"

# Secret Manager Accessor
gcloud projects add-iam-policy-binding ${PROJECT_ID} \
  --member="serviceAccount:${CLOUDBUILD_SA}" \
  --role="roles/secretmanager.secretAccessor"
```

### 4️⃣ Cloud Run Service Account Oluştur

```bash
gcloud iam service-accounts create hss-backend \
  --display-name="HSS Backend Service Account" \
  --project=hss-cloud-473511

# Secret Manager accessor izni ver
gcloud projects add-iam-policy-binding hss-cloud-473511 \
  --member="serviceAccount:hss-backend@hss-cloud-473511.iam.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor"
```

### 5️⃣ Cloud Build Trigger Oluştur (Tag-Based)

Google Cloud Console'dan:
1. **Cloud Build > Triggers** sayfasına gidin
2. **Create Trigger** butonuna tıklayın
3. Ayarlar:
   - **Name:** `hss-backend-tag-trigger`
   - **Event:** `Git tag`
   - **Tag:** `v.*` (regex)
   - **Source:** Repository seçin
   - **Branch:** `^main$`
   - **Configuration:** `Cloud Build configuration file (yaml or json)`
   - **Location:** `hss-backend/cloudbuild.yaml`
4. **Create** butonuna tıklayın

### 6️⃣ Deployment Test

Tag oluşturup push ederek test edin:

```bash
git tag -a v1.0.0 -m "Production release v1.0.0"
git push origin v1.0.0
```

Build durumunu kontrol edin:
```bash
gcloud builds list --project=hss-cloud-473511 --limit=5
```

## 🔍 Önemli Ayarlar (cloudbuild.yaml)

### Substitutions (Değiştirilecek Değerler)

```yaml
_PROJECT_ID: 'hss-cloud-473511'
_REGION: 'europe-west3'
_SERVICE_NAME: 'hss-backend'
_SPRING_PROFILE: 'prod'
_SQL_INSTANCE_CONNECTION_NAME: 'hss-cloud-473511:europe-west3:hss-sql'
_STORAGE_BUCKET: 'hss-files'
_JWT_ISSUER_URI: 'https://securetoken.google.com/hss-cloud-473511'
_FIREBASE_PROJECT_ID: 'hss-cloud-473511'
```

### Environment Variables

Cloud Run deployment'ında otomatik olarak ayarlanan environment variables:

- `SPRING_PROFILES_ACTIVE=prod`
- `GCP_SQL_INSTANCE_CONNECTION_NAME=hss-cloud-473511:europe-west3:hss-sql`
- `GCP_PROJECT_ID=hss-cloud-473511`
- `GCP_STORAGE_BUCKET=hss-files`
- `JWT_ISSUER_URI=https://securetoken.google.com/hss-cloud-473511`
- `FIREBASE_PROJECT_ID=hss-cloud-473511`

### Secrets (Cloud Run'da otomatik mount edilir)

- `DB_USERNAME`
- `DB_PASSWORD`
- `FIREBASE_SERVICE_ACCOUNT_KEY`

## 📊 Cloud Run Resource Limits

```yaml
_MEMORY: '2Gi'
_CPU: '2'
_TIMEOUT: '3600'        # 1 saat
_MAX_INSTANCES: '10'
_MIN_INSTANCES: '1'
_PORT: '8080'
```

## 🔗 İlgili Dosyalar

- `cloudbuild.yaml` - Cloud Build pipeline config
- `Dockerfile` - Container build config
- `src/main/resources/application-prod.yaml` - Production Spring config

## 🐛 Sorun Giderme

### Build başarısız olursa

```bash
# Build loglarına bak
gcloud builds log <BUILD_ID> --project=hss-cloud-473511

# Son build'e bak
gcloud builds list --project=hss-cloud-473511 --limit=1
```

### Secret erişim problemi

```bash
# Service account izinlerini kontrol et
gcloud projects get-iam-policy hss-cloud-473511 \
  --flatten="bindings[].members" \
  --filter="bindings.members:serviceAccount:hss-backend@*"
```

### Cloud Run deploy başarısız

```bash
# Cloud Run service'i kontrol et
gcloud run services describe hss-backend \
  --region=europe-west3 \
  --project=hss-cloud-473511

# Logları kontrol et
gcloud logging read "resource.type=cloud_run_revision AND resource.labels.service_name=hss-backend" \
  --limit=50 \
  --project=hss-cloud-473511
```

## ✅ Checklist

- [ ] Artifact Registry repository oluşturuldu
- [ ] Secret Manager secrets oluşturuldu (db-username, db-password, firebase-service-account)
- [ ] Cloud Build IAM izinleri verildi
- [ ] Cloud Run service account oluşturuldu
- [ ] Cloud Build trigger oluşturuldu
- [ ] Test deployment yapıldı
- [ ] Cloud Run URL: `https://hss-backend-XXXXX.run.app`

## 📞 Yardım

Sorun yaşarsanız:
1. Build loglarına bakın: `gcloud builds list`
2. Cloud Run loglarına bakın: Google Cloud Console > Cloud Run > Logs
3. Secret Manager'da secret'ları kontrol edin
4. IAM izinlerini kontrol edin
