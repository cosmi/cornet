# Cornet

A simple implementation of assets pipeline compatibile with Ring and Compojure. It is kept in the spirit of Compojure. 
You can use assets from many configurable sources and compose transformations in a nice, functional, middleware based way.

![Assets pipeline](http://upload.wikimedia.org/wikipedia/commons/6/6d/Cornet2.png)

 Your `project.clj` dependency:
```clojure
[cornet "0.1.0"]
```
or find it in [Clojars](https://clojars.org/cornet).

Also check out [Scijors](http://github.com/cosmi/scijors) - a templating library.

## Features

Currently it will help you with the following tasks:
- compilation of LessCSS,
- compilation of CoffeeScript,
- minification of JavaScript (UglifyJS) and CSS (YUI Compressor).

Cornet works in two modes:
- in dev mode it will recompile the assets every time the source changed. In case of LessCSS, it will also check the imported files!
- in production mode, the compilation will happen on either server start or on the first load of given asset (your choice!).

It is able to work with either files in your filesystem, or resources (from Java classpath or clojure.java.io/resource), or both.
Thanks to that, you can use assets from your project's dependencies.

Cornet does not require any native libraries - so it should deploy everywhere out of the box!



## Why Cornet?

I wasn't happy with any other Clojure implementation of those features - in fact, I know of only one: 
[Dieter](https://github.com/edgecase/dieter) and its refreshed version, [Stefon](https://github.com/circleci/stefon).
Those are based on Ruby's Sprockets library - which is a nice abstraction for an Object-Oriented, MVC based framework like Ruby on Rails,
but in my opinion does not work well with composable, functional one like Compojure.

First, simple, nameless library for our in-house projects was created.
Then, I tried to build a bigger framework, [Causeway](http://github.com/cosmi/causeway). It works nicely, but is big and bulky, and the asset pipeline is only a small part of it.
Finally, all my experience with creating asset pipelines for Clojure has been put into Cornet. And I believe it is the ultimate thing (although not so many features are implemented yet).

## Usage

### Installation

In your project.clj dependencies add:

```clojure
[cornet "0.1.0"]
```

### Basic usage

In your ns declaration add:

```clojure
(:use cornet.core cornet.route)
```

Sample compojure handler:

```clojure
(defroutes app-routes
  (GET "/" [] "Hello World")
  (wrap-url-response
         (some-fn
             (compiled-assets-loader "precompiled"
                                     :lesscss-list ["css/bootstrap.less"]
                                     :coffee-list ["js/example.coffee"]
                                     :mode :prod ; or :dev for minification
                                     )
             (static-assets-loader "public" 
                                     :mode :prod)))
  (route/not-found "Not Found")))
```

The `(static-assets-loader "public")` will load all static assets and replaces `(compojure.route/resources "public")`.



### Files layout

For this tutorial, put your LessCSS and CoffeeScript assets in `resources/precompiled` directory, 
and your csses and javascripts in `resources/public`.

For example use the following directory structure:

```
resources
+ public
  + css
    - style.css
+ precompiled
  - files.list
  + css
    - bootstrap.less
    + bootstrap
      - bootstrap.less
      - variables.less
      - mixins.less
      .....
  + js
    - example.coffee
```

### Advanced usage

Assets loaders return java.net.URL objects or nils, if the file is not found. This can be handled by Compojure with Ring,
but preferably you should wrap them in `wrap-url-response` from `cornet.route` namespace.

#### Compiled assets:

```clojure
(compiled-assets-loader "precompiled" ; this is path prefix
  ;; List LessCSS files to compile
  :lesscss-files ["css/bootstrap.less"]
  ;; List CoffeeScript files to compile
  :coffee-files ["js/example.coffee"]
  ;; List of files can also be put into a file instead - combine lesscss and coffeescript files in one file list:
  :fileset-file "files.list"
  ;; There are two modes are :dev and :prod. Dev mode will recompile on file change and will not minify output
  :mode :dev
  ;; Normally Cornet takes files from classpath. If you want to get them from filesystem, use this flag:
  :from-filesystem true
  ;; You can force or disable minification by using this flag. Otherwise, the mode will be used.
  :minify true
                                     )
```

#### Static assets:

```clojure
(static-assets-loader "public" ; this is path prefix
  ;; There are two modes are :dev and :prod. Dev mode will not minify output.
  :mode :dev
  ;; Normally Cornet takes files from classpath. If you want to get them from filesystem, use this flag:
  :from-filesystem true)
```

#### Cornet mode:

Default Cornet's mode is `:prod`. There are three ways to change Cornet's mode (in order of increasing priority):

##### JVM Option:
Set JVM flag `-DcornetMode=dev` to set dev mode. To do that, add the following code in `project.clj`:
```clojure
:jvm-opts ["-DcornetMode=dev"]
```

##### `with-cornet-mode` macro:
Wrap the handler in `with-cornet-mode` from `cornet.mode` namespace. For example:

```clojure
(cornet.mode/with-cornet-mode :dev
  (static-assets-loader "public"))
```

##### Pass explicit `:mode` flag:

```clojure
(static-assets-loader "public" :mode :dev)
```

## FAQ

### "Why did you choose to group LESS and CoffeeScript compilation and resource minification in one middleware function?"

For convenience's sake only! You can shape your middleware in any way you want. For example:

```clojure
(use '[cornet.processors lesscss coffeescript minifiers] 'cornet.loaders)

(defroutes app-routes
  (GET "/" [] "Hello World")
  (wrap-url-response
   (let [loader (resource-loader "/precompiled")]
     (->
       (some-fn (wrap-lesscss-processor loader :mode :dev)
                (wrap-coffeescript-processor loader :mode :dev)
                (resource-loader "/public"))
       (cond-> (not devmode)
         (->
           (wrap-yui-css-compressor)
           (wrap-uglify-js-compressor)))))))
```

### "How is the cache working?"

Currently Cornet writes all transformed files as temporary files 
(using java.io.File/createTempFile - it will be /tmp on most systems). 
I am planning on adding in-memory processing and cache.

### "Are you planning to add more compilers?"

Yeah, we plan to have at least SASS implemented.
Also we would like to have ClojureScript covered!

### "I need a feature not yet implemented. Can I contribute?"

Yes, please!

## License

Copyright © 2013 Marcin Skotniczny & [Software Mansion](http://software-mansion.com)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
