# TODO
* Rewrite Ball
* Info text
* Some special ability for balls
  * No death for a certain period
  * Make all the ball feels like gravity
  * Add life.

# About
Collision is a game created by Sun. The goal of the game is to move a user-controlled ball to
avoid being collided with the other balls. The game features [Leap Motion](https://www.leapmotion.com/) technology, and
it is created for the show of our [SSAST](http://ssast.org).

## Authors
* [Sun Ziping](http://sunziping.com)
* Authors from the project [polygame](https://github.com/mariosangiorgio/polygame). Thanks a lot for their source code.
* Authors from the project [kollision](https://www.kde.org/applications/games/kollision). Thanks a lot for their idea.

# Developer Information
## Platform
It's developed with Intellij and JDK 1.8 under Windows. Other platforms haven't been tested yet.

## Dependence
* Leap Motion SDK (available [here](https://developer.leapmotion.com/downloads))
  * `lib/Leap.dll`
  * `lib/LeapJava.dll`
  * `lib/LeapJava.jar`
* jBox2d (available [here](https://github.com/jbox2d/jbox2d)).
  * `lib/jbox2d-library.jar`

Notice: Because jBox2d's `velocityThreshold` is set too low to satisfy my game. I've changed the value from `1.0f` to `0.01f` and
recompiled it.
