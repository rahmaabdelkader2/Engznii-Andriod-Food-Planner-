# Engznii-Andriod-Food-PLanner-
[Trello Board](https://trello.com/b/7K1n0Gzg/engznii-food-planner)

[APP APK](https://drive.google.com/drive/folders/1SKA9hRcp6If8IY2t4t852TqdGwg2bpiv?usp=drive_link)

# Engzni Food Planner

Engzni Food Planner is an Android application designed to simplify meal planning and culinary discovery. Plan your weekly meals, explore diverse cuisines, and save your favorite recipes - all in one place.

## Features

### 🍽️ Meal Discovery
- Daily featured "Meal of the Day"
- Meals' Suggestions based on your country
- Search by category, country, or ingredient
- Browse comprehensive meal collections
- Detailed meal views with:
  - High-quality images
  - Origin country
  - Ingredients with measurements
  - Step-by-step instructions
  - Embedded cooking videos

### 📅 Meal Planning
- Weekly meal planner
- Save favorite meals for quick access
- Offline access to saved meals and plans
- Cloud synchronization across devices

### 🔐 User Experience
- Multiple login options:
  - Email/password
  - Social authentication (Google)
  - Guest mode
- Personalized Home screen
- Beautiful Lottie animations

## Technical Details

### Architecture
- MVP 

### Technologies
- **Java** - Primary development language
  - Room - Local database
  - ViewModel - Lifecycle-aware data management
  - LiveData - Observable data holders
  - Navigation Component - In-app navigation
- **Retrofit** - REST API communication
- **Firebase**:
  - Authentication (Email/Google)
  - Firestore - Cloud synchronization
- **Lottie** - Splash screen animations
- **Glide** - Image loading
- **ExoPlayer** - Video playback

### API Integration
The app uses [TheMealDB API](https://www.themealdb.com/api.php) for all meal data.

### Setup
- Clone the repository:
   git clone https://github.com/yourusername/engzni-food-planner.git
   
### note 
-  This application was developed as part of [Andriod with java course/ at ITI 45 embedded system ] and demonstrates modern Android development practices including API integration, local storage solutions, and user authentication flows  
