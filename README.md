# WinHam ARCO Controller Manager 
## rotctld Interface
Implements the HamLib rotctld network interface. Essentialy a protocol converter between Yaesu GS-232A and HamLibs rotctld.

Written in Scala to allow write-once run anywhere.

*This should be added to HamLib but I haven't written any C code for about 20 years! And I want to use my new Rotator Genius with MacLoggerDX now.*


## Building
### Requiments
- java JDK with JavaFx Avaialable from bellsoft. https://bell-sw.com/pages/downloads/ 
  - Select 21.0.3.fx-librca
  
- sbt See https://www.scala-sbt.org

### Build command
For a local build:
`% sbt universal:packageBin`
This should produce :
`target/universal/rgrotctld-0.0.1.zip`

To build a release (requires github git permissions):

`% sbt release`

To build a jar file:

`% sbt assembly`

builds rotatorcontrol.jar in ./target/result


### Install via zip file
1. Unzip the rgrotctld-x.x.x.zip file. 
2. Navigate to the extracted bin directory.
3. Invoke command line:

`Mac or Linux:  ./RotatorControl`

`Windows: RotatorControl`



###Status
This the beginning of a work-in-progress.
- Works with MacLoggerDX. 

Source code at https://github.com/dicklieber/RotatorControl

###Useful Info
Using
- ScalaFx https://www.scalafx.org
- JFreeChart https://www.jfree.org/jfreechart/

JFreeChart Compass: http://www.java2s.com/Code/Java/Chart/JFreeChartCompassChartSample.htm

