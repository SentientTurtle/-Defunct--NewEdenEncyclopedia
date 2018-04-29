# New Eden Encyclopedia

The New Eden Encyclopedia project is a set of tools to automatically generate wiki-esque information pages for items & objects in the video game EVE Online.

## Setup & Usage

1) Download the icons section of the EVE Online Image Export Collection and place the "itemicons" directory in the project's /rsc/ directory
2) (Steps 2-4 Optional) Place the "evegr2toobj" executable in the rsc/grannytemp/evegr2toobj directory. (This utility executable can be found on the EVE Online forums) 
3) Dump the EVE Online models to the /rsc/grannytemp/dx9 directory.
4) Run the `net.sentientturtle.nee.util.GrannyParser#main` method.
5) Run the `net.sentientturtle.nee.Main#main` method to generate pages.

## Requirements

* [Sqlite4java v392](https://bitbucket.org/almworks/sqlite4java)
* [JSON in Java v20180130](https://github.com/stleary/JSON-java)
* [Apache Commons Compress v1.16.1](https://commons.apache.org/proper/commons-compress/)
* [Apache Commons IO v2.6](http://commons.apache.org/proper/commons-io/)
* [IntelliJ Annotations v12](https://www.jetbrains.com/help/idea/annotating-source-code.html)

## Notes

This project is used to experiment with various language features, notably Java 8 streams.
As result, very little concern has been taken for the memory footprint, and page generation speed.

Additionally, the project has been heavily refactored and partially rewritten, leaving large parts of the code without explicit documentation.

## License

All rights reserved.