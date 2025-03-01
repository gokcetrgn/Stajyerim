# StajYerim - Mobil Uygulama

StajYerim, stajyer arayan şirketler ile staj yapmak isteyen öğrencileri bir araya getiren bir mobil uygulamadır. Kotlin dili ile geliştirilmiş olup, Firebase altyapısı ve Jetpack Compose kullanılarak modern bir kullanıcı deneyimi sunmaktadır.

## 📌 Özellikler
- **Şirketler için:** Staj ilanı oluşturma ve başvuruları yönetme.
- **Öğrenciler için:** Staj ilanlarını görüntüleme ve başvuru yapma.
- **Güvenli Kimlik Doğrulama:** Firebase Authentication ile kullanıcı giriş/çıkışı.
- **Gerçek Zamanlı Veri Yönetimi:** Firebase Firestore ile ilan ve başvuruların anlık takibi.
- **Modern Arayüz:** Jetpack Compose ile dinamik UI tasarımı.

## 🛠 Kullanılan Teknolojiler
- **Dil:** Kotlin
- **UI:** Jetpack Compose
- **Veritabanı:** Firebase Firestore
- **Kimlik Doğrulama:** Firebase Authentication
- **Depolama:** Firebase Storage

## 🚀 Kurulum
1. **Projeyi klonla:**
   ```bash
   git clone https://github.com/kullanici/stajyerim.git
   cd stajyerim
   ```
2. **Firebase projesini yapılandır:**
   - Firebase Console'a gir ve yeni bir proje oluştur.
   - `google-services.json` dosyanı `app/` klasörüne ekle.
3. **Gerekli bağımlılıkları yükle:**
   ```bash
   ./gradlew build
   ```
4. **Uygulamayı çalıştır:**
   ```bash
   ./gradlew installDebug
   ```

## 💡 Katkıda Bulunma
Katkıda bulunmak için lütfen bir **pull request (PR)** gönderin veya bir **issue** açın.

## 📜 Lisans
Bu proje MIT lisansı ile lisanslanmıştır. Daha fazla bilgi için [LİSANS](LICENSE) dosyasına göz atabilirsiniz.

