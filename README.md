# Defunct

Project no longer maintained

---

# New Eden Encyclopedia

The New Eden Encyclopedia project is a set of tools to automatically generate wiki-esque information pages for items & objects in the video game EVE Online.

## Setup & Usage

1) Download the icons section of the EVE Online Image Export Collection and place the "itemicons" directory in the project's /rsc/ directory
2) (Steps 2-4 Optional) Place the "evegr2toobj" executable in the rsc/grannytemp/evegr2toobj directory.
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

This project is used to experiment with various Java language features.
As result, very little concern has been taken for the memory footprint, and page generation speed.

Additionally, the project has been heavily refactored and partially rewritten, leaving large parts of the code with minimal documentation.

## License

Code under MIT, subject to copyright and trademark notices below.

## Copyright & trademark notices

EVE Online and the EVE logo are the registered trademarks of CCP hf.
EVE Online, the EVE logo, EVE and all associated logos and designs are the intellectual property of CCP hf.
All artwork, screenshots, characters, vehicles, storylines, world facts or other recognizable features of the intellectual property relating to these trademarks are likewise the intellectual property of CCP hf.
CCP hf. has granted permission to New Eden Encyclopedia to use EVE Online and all associated logos and designs for promotional and information purposes on its website but does not endorse, and is not in any way affiliated with, New Eden Encyclopedia.
CCP is in no way responsible for the content on or functioning of this website, nor can it be liable for any damage arising from the use of this software.

Use of EVE Online materials in accordance with [CCP Games' third-party-developer license](https://developers.eveonline.com/license-agreement)


'Granny3D' is the registered trademark of Epic Games Tools LLC.
Trademark only used nominatively; No Granny3D middleware is included in this repository.