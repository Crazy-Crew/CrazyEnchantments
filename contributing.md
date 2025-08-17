# Contributing to CrazyEnchantments
Contributions to the project are always welcome, Pull Requests do have some guidelines before being approved.

### Prerequisites
A list of things necessary to make sure you can build CrazyEnchantments properly.
* Java 21 ( Adoptium or Corretto is recommended )
* Intellij IDEA
    * > https://www.jetbrains.com/idea/download/
* Gradle
* Brain
* `git`

### Installation
1. Clone the repo
   ```sh
   git clone https://github.com/Crazy-Crew/CrazyEnchantments.git
   ```
2. Open the repository using Intellij IDEA & wait until it finishes downloading/indexing.
3. Run the build task.
   ```gradle
   ./gradlew assemble
   ```
4. Look in `jars` folder

### You should always create the fork as a personal repository not in an organization.
Any pull request made by a fork in an organization prevents modifications. Everyone has their own way of doing things and rather asking you to change that. A personal fork lets us change the things
that we have a tick about.

If you do not use a personal fork, We have to manually merge your pull request which means it's marked as closed instead of merged.

Pull Requests must be labeled properly according to if it's a bug fix, a new feature or enhancements to the code base.
 * `git checkout -b fix/your_fix`
  * `git checkout -b feature/your_feature`
  * `git checkout -b quality/your_enhancement`
 * Commit your changes using `git commit -m 'your commit'`
 * Push to your branch using `git push`
 * Open a pull request to the `dev` branch on our repository to add your change. 

You must explain what your pull request is changing and if needed, Supply a video of your change as Pull Requests are a way to get feedback.
