# CollaborAnt

## Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
3. [What's New in This Release](#whats-new-in-this-release)
4. [Installation](#installation)
5. [Usage](#usage)
6. [Screenshots](#screenshots)
7. [Contributing](#contributing)
8. [License](#license)
9. [Contact](#contact)

## Introduction
**CollaborAnt** is a mobile application designed to enhance teamwork by improving collaboration and interaction among team members. Inspired by the collaborative nature of ants, CollaborAnt provides a platform where users can work together seamlessly, share ideas, and manage projects effectively.

## Features
- **Real-time Collaboration:** Work together with your team in real-time.
- **Task Management:** Create, assign, and track tasks easily.
- **Communication:** Built-in chat and messaging features to keep everyone in sync.
- **File Sharing:** Share documents, images, and other files within the app.
- **Notifications:** Stay updated with real-time notifications on task updates and messages.
- **User-friendly Interface:** Intuitive and easy-to-navigate UI.

## What's New in This Release (lab5)
### Firebase Google Authentication
We have implemented user authentication following Firebase guidelines. Each time a user authenticates, a unique UUID is created. This UUID is crucial for maintaining a secure and efficient authentication system.

### Firebase DB Interaction
## 1. User Information Management

We created a `users` collection to store comprehensive information about each user. Each document within this collection includes properties such as:
- Name
- Profile photo URL or color of the monogram
- Address
- UUID (provided by Google)
- JoinedTeams
- Nickname
- Telephone
- Location
- Email

This structure ensures a one-to-one relationship between user authentication and user information.
Due to the 1 MB size limit for documents in Firestore, we utilize Firebase Storage for saving user profile images. Each user's image is stored with a filename that matches the user's unique UUID, ensuring easy retrieval and management.
Each user has a subcollection named `kpivalues` within their document to store the current KPI data for each team they are working with. This allows us to efficiently track and manage individual performance metrics.

## 2. Team Management

We created a `teams` collection where each document represents a team and contains:
- Description
- Image or color of the monogram
- list of memebers
- list of roles
- Name of the team
- Unread message flag

Each team document also has a subcollection named `chat`, which stores the current messages exchanged within the team. 
We use the Firestore-generated ID for the team to generate links or QR codes for team invitations.
Every time a user uses a link to join a team, their information is added to the list of members, and their initial role is set to "Junior Member" by default.

## 3. Task Management

A separate `tasks` collection was established to manage tasks. Each task document includes details such as:
- Title
- Categories
- Due date
- End Date
- Repeat
- Tag
- Team id
- Parent id (if is a recurrent task)
-  Delegated Members to the task
- Other relevant task information

Within each task document, there are several subcollections:
- `history`: Contains a list of actions related to the task.
- `comments`: Stores all related messages for the task.

To work with attachments, we use Firebase Storage to handle files related to the task properly.

### New UI Palette
We've introduced a fresh new color palette to improve the visual appeal and usability of the app. Here’s a preview of the new palette:

<p align="center">
  <img src="/img/palette.jpeg" alt="edit" width="300"/>
</p>

Full color palette for both Light and Dark Themes:
- **Primary Color:** #FA9B1E
- **Primary Dark Color:** #FCCD85
- **Primary Variant Color:** #EF8019
- **Secondary Color:** #8CC5FF
- **Secondary Variant Color:** #2937B3
- **Background Color:** #F8F8F8
- **Background Dark Color:** #121212
- **Error Color:** #B00020
- **Error Color:** #CF6679

## Installation
1. **Clone the repository:**
   ```bash
   git clone https://github.com/polito-MAD-2024/lab5-g15.git
   ```
   or via ssh
2. ```bash
   git clone git@github.com:polito-MAD-2024/lab5-g15.git
   ```
2. **Open Android Studio**:
- Select **Open an existing Android Studio project**.
- Navigate to the cloned directory and select it.
3. **Install dependencies**:
- Ensure you have the latest version of Android Studio and all necessary SDKs.
- Sync the project with Gradle files.
4. **Run the application**:
- Connect your Android device or start an emulator.
- Click on the **Run** button in Android Studio.

## Usage

- **Config Google Account**: The device must already have been associated with a google account through browser or settings.
- **Login:** Open the application, click on Login and select the previously configured google account.
- **Create a Team:** Set up a new team and invite members.
- **Start Collaborating:** Begin creating tasks, sharing files, and communicating with your team.

## Screenshots

*Include some screenshots of your app here to give users a visual idea of what to expect.*

## License

CollaborAnt is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

## Contact

For any inquiries or support, please contact us at madcollaborant@gmail.com.
