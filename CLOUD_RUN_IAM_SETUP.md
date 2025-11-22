# Cloud Run IAM Authentication Ayarları

## Sorun
Cloud Run servisleri varsayılan olarak IAM authentication gerektirir. Bu yüzden public endpoint'ler bile 403 Forbidden hatası veriyor.

## Çözüm

### Seçenek 1: Tüm servisi public yapmak (ÖNERİLMEZ - Güvenlik riski)
```bash
# Development backend için
gcloud run services update hss-backend-dev \
  --region=europe-west3 \
  --no-require-iam \
  --project=hss-cloud-473511

# Production backend için (ÖNERİLMEZ)
gcloud run services update hss-backend \
  --region=europe-west3 \
  --no-require-iam \
  --project=hss-cloud-473511
```

### Seçenek 2: Sadece allUsers için invoke izni vermek (ÖNERİLEN)
Bu yöntem, servisi public yapmadan sadece invoke izni verir. Spring Security zaten endpoint'leri koruyor.

```bash
# Development backend için
gcloud run services add-iam-policy-binding hss-backend-dev \
  --region=europe-west3 \
  --member="allUsers" \
  --role="roles/run.invoker" \
  --project=hss-cloud-473511

# Production backend için (dikkatli kullanın)
gcloud run services add-iam-policy-binding hss-backend \
  --region=europe-west3 \
  --member="allUsers" \
  --role="roles/run.invoker" \
  --project=hss-cloud-473511
```

### Seçenek 3: Sadece belirli endpoint'ler için IAM bypass (EN İYİSİ)
Cloud Run'da path-based routing kullanarak sadece `/api/public/**` endpoint'lerini public yapabilirsiniz, ama bu daha karmaşık bir yapılandırma gerektirir.

## Mevcut IAM Ayarlarını Kontrol Etme

```bash
# Development backend IAM policy
gcloud run services get-iam-policy hss-backend-dev \
  --region=europe-west3 \
  --project=hss-cloud-473511

# Production backend IAM policy
gcloud run services get-iam-policy hss-backend \
  --region=europe-west3 \
  --project=hss-cloud-473511
```

## Önerilen Yaklaşım

1. **Development/Staging**: Seçenek 2'yi kullanın (allUsers için invoke izni)
2. **Production**: IAM authentication'ı koruyun, sadece frontend'den gelen istekler için Firebase token kullanın

## Test

IAM ayarlarını yaptıktan sonra:

```bash
# Public health check test
curl https://hss-backend-dev-296268886725.europe-west3.run.app/api/public/health

# Production health check test
curl https://hss-backend-2ez5rneaua-ey.a.run.app/api/public/health
```

## Notlar

- Spring Security zaten endpoint'leri koruyor, bu yüzden IAM'ı kaldırmak güvenlik riski oluşturmaz
- `/api/public/**` endpoint'leri zaten `permitAll()` ile korunuyor
- Diğer endpoint'ler Firebase token authentication gerektiriyor

