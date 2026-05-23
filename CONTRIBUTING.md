# Contributing

Thanks for helping improve Essentialist.

## Before You Start

- Keep changes focused. One feature or fix per pull request is easier to review.
- Open an issue first for large features, behavior changes, or new modules.
- Use clear names for commands, permissions, configuration keys, and messages.

## Local Setup

You need JDK `25`.

```bash
git clone https://github.com/HanielCota/Essentialist.git
cd Essentialist
./gradlew build
```

## Code Style

The project uses Spotless with google-java-format.

```bash
./gradlew spotlessApply
./gradlew spotlessCheck
```

## Pull Requests

Before opening a pull request, run:

```bash
./gradlew build
```

Include a short summary, explain why the change is needed, and mention how it was tested.
