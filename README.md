# Cornet

A simple implementation of assets pipeline compatibile with Ring and Compojure. It is kept in the spirit of Compojure. 
You can use assets from many configurable sources and compose transformations in a nice, functional, middleware based way.

![Assets pipeline](http://upload.wikimedia.org/wikipedia/commons/6/6d/Cornet2.png)

Currently it will help you with the following tasks:
- compilation of LessCSS,
- compilation of CoffeeScript,
- minification of JavaScript (UglifyJS) and CSS (YUI Compressor).

Also check out [Scijors](http://github.com/cosmi/scijors) - a templating library.


## Why Cornet?

I wasn't happy with any other Clojure implementation of those features - in fact, I know of only one: 
[Dieter](https://github.com/edgecase/dieter) and its refreshed version, [Stefon](https://github.com/circleci/stefon).
Those are based on Ruby's Sprockets library - which is a nice abstraction for an Object-Oriented, MVC based framework like Ruby on Rails,
but in my opinion does not work well with composable, functional one like Compojure.

First, simple, nameless library for my own projects was created.
Then, I tried to build a bigger framework, [Causeway](http://github.com/cosmi/causeway). It works nicely, but is big and bulky, and the asset pipeline is only a small part of it.
Finally, all my experience with creating asset pipelines for Clojure has been put into Cornet. And I believe it is the ultimate thing (although not so many features are implemented yet).

## Usage

Coming soon...

## Basics

Cornet works in two modes:
- in dev mode it will recompile the assets every time the source changed. In case of LessCSS, it will also check the imported files!
- in production mode, the compilation will happen on either server start or on the first load of given asset (your choice!).

It is able to work with either files in your filesystem, or resources (from Java classpath or clojure.java.io/resource), or both.
Thanks to that, you can use assets from your project's dependencies.

Cornet does not require any native libraries - so it should deploy everywhere out of the box!

## License

Copyright © 2013 Marcin Skotniczny & [Software Mansion](http://software-mansion.com)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
