# Compile
The project uses [LibGDX](http://libgdx.badlogicgames.com/index.html) for the visualization. For most questions the [official documentation](https://github.com/libgdx/libgdx/wiki) holds an answer.

To run the destop version in an IDE you have to run the class `DesktopLauncher` and set your working directory to `android/assets`. To compile and run it from the command line use `gradlew desktop:run`. To create a .jar use `gradlew desktop:dist`.