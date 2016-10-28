ubiquitous-capture
==================

[![Join the chat at https://gitter.im/mpcjanssen/ubiquitous-capture](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/mpcjanssen/ubiquitous-capture?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Capture scribbles as images to your SDCard. Meant for GTD ubiquitous capture.

Small application to capture scribbles to the SD card. It is created to be easily accessible. This is achieved by:

* Permanent notification for easy and ubiquitous access.
* Closing or saving will store the scribble as PNG and close the app.

Code for the canvas is based on a sample from http://www.mysamplecode.com/

Changelog
=========

1.0.4
-----

* Add setting for stylus only drawing.

1.0.3
-----

* Fix folder selection.
* Pad date parts with zero. 
* Add setting to change permanent notification priority.

1.0.2
-----

* Fix issue where switching back could clear the capture.
* Fix NPE in settings.
* Sort folders when setting capture path.

1.0.1
-----

* Allow setting the capture directory.

1.0.0
-----

* Update UI (idea Robert Chudy).
* Implemented redo.
* Changed default to keep app open after saving.

0.0.7
-----

* Updated icons, many thanks to Robert Chudy.

0.0.6
-----

* Added setting for closing when saving the image.

0.0.5
-----

* Added undo.

0.0.4
-----

* Let MediaScanner scan new png's so they will show up in Gallery for review.
* Lower API level to support more devices.

0.0.3
-----

* Start notification on boot
* Reduce memory usage

0.0.2
-----

* Don't clear canvas when switching to other app without saving.

0.0.1
-----

* Initial release
