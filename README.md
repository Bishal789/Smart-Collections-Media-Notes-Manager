# Smart Collections – Media & Notes Manager

## Overview
Welcome to Smart Collections! This is a JavaFX desktop application designed to help you manage your notes, media, and documents efficiently. Whether you're organizing study materials, keeping track of tasks, or previewing media files, Smart Collections has you covered.

---

## Features
Here's what Smart Collections can do for you:
- **Recursive Folder Import**: Import files from folders and subfolders effortlessly, while avoiding duplicates.
- **Tagging and Keyword Indexing**: Quickly search and rank results based on frequency.
- **Recently Viewed Stack**: Easily navigate back to items you've recently accessed.
- **Undo Stack**: Undo delete or edit operations with a single click.
- **Task Queue**: Manage your study tasks with priorities to stay on top of your goals.
- **Persistence**: Save and load your library, indices, and tasks seamlessly.
- **Media Preview**: Play audio and video files with intuitive controls.
- **Duplicate Prevention**: Avoid clutter by preventing duplicate items in your library.
- **Error Handling**: Gracefully handle unsupported file types without interruptions.

---

## Build Instructions
Getting started is simple! Follow these steps to build the project:
1. Make sure you have **Java 22** and **JavaFX 22** installed on your system.
2. Clone this repository or download the source code.
3. Open a terminal and navigate to the project directory.
4. Run the following command to build the project:
   ```bash
   ./gradlew build
   ```

That's it! The project will be built and ready to run.

---

## Run Instructions
You can run Smart Collections in two ways:

### Using Gradle
1. Open a terminal in the project directory.
2. Run the application directly using Gradle:
   ```bash
   ./gradlew run
   ```

### Using the Runnable JAR
1. After building the project, navigate to the `build/libs` directory.
2. Run the JAR file with the following command:
   ```bash
   java -jar FinalProject-1.0-SNAPSHOT.jar
   ```

Enjoy managing your collections!

---

## Known Issues
Currently, there are no known issues. If you encounter any problems, feel free to report them.

---

## Requirements
- **Java Version**: 22
- **JavaFX Version**: 22

---

## Checklist
Here's a quick summary of the features implemented in Smart Collections:
- [x] Recursive folder import
- [x] Tagging and keyword indexing
- [x] Recently viewed stack
- [x] Undo stack
- [x] Task queue
- [x] Persistence with Object I/O
- [x] Media preview with controls
- [x] Duplicate prevention
- [x] Error handling for unsupported files

Thank you for using Smart Collections! We hope it makes managing your media and notes a breeze.
