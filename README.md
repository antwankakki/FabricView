# FabricView - [![Build Status](https://travis-ci.org/antwankakki/FabricView.svg?branch=master)](https://travis-ci.org/antwankakki/FabricView) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-FabricView-blue.svg?style=flat)](http://android-arsenal.com/details/1/2616)

A new canvas drawing library for Android. The library was born as part of a project in SD Hacks (www.sdhacks.io) on October 3rd. It is currently under construction and will be refactored and polished in the coming weeks! Stay tuned.

*Why?* From Doodling on the screen and unleashing the user's creativity to capturing his signature for authentication purposes... The limit is your imagination. 

### Sample Screen Recording
![Screenshot1](http://i.imgur.com/9nME9Yt.gif)

### How to use? As easy as 1, 2, 3!
Please note this is an alpha build, not feature complete, and can be buggy. If you find any bugs, report them to us here!  
  1. Add JitPack to your build.gradle at the end of repositories
  
     ```javascript
    allprojects {
        repositories {
            jcenter()
            maven { url "https://jitpack.io" }
            ...
        }
    }
     ```
  2. Add FabricView to your dependencies
    
     ```javascript
       	dependencies {
                compile fileTree(include: ['*.jar'], dir: 'libs')
       	        compile 'com.github.antwankakki:FabricView:latest'
                ...
       	}
     ```
  3. Add the following to your layout xml file
  
     ```XML
      <com.agsw.FabricView.FabricView
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:id="@+id/faricView"
          android:layout_centerHorizontal="true"
          android:layout_alignParentBottom="true" />
     ```
  4. You are done! You can now reference it in your classes and do all kinds of cool things with it. Check out the api to see what it can do at http://antwankakki.github.io/FabricView/javadoc/

### 0.1 Milestone Progress
 * Multiple Input Colors
 * Multiple standard background modes (Notebook style, Graph paper...)
 * Export canvas as image
 * Performance (Redraw only parts of the screen that need to be redrawn)
 * Background Support
 * Images support
 * Undo/Redo Support - ToDo
 * Drawing Text captured directly from keyboard. - ToDo
 * Allowing Resize when inserting images. - ToDo
 * onCanvasChanged Event - ToDo
 
 
### Future plans
 * Layers and Groups
 * More complex objects
 * Transparency
 * Rotations, translations, scalings

### Thanks to...
 * Image support [dbachelder](https://github.com/dbachelder)
 * getCanvasBitmap bugfix [eling13](https://github.com/eling13)
