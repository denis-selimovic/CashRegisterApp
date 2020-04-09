# CashRegisterApp

## Description
JavaFX cross-platform desktop app

## Installation

### Executable JAR

Follow these steps to run CashRegisterApp on your platform.
* Download and install [Java](https://www.oracle.com/java/technologies/javase-jdk14-downloads.html) (JDK 14 is required)
* Make sure your default JRE is set to version 14
* Download executable JAR from [here](https://github.com/dselimovic1/CashRegisterApp/releases/latest)
* Execute JAR from terminal with __java -jar <jar_name>__

You can run executable JAR on double click on all platforms (you still need JDK 14 and default JRE set to version 14). 
* On Windows this works out of the box
* On Ubuntu you must give permission to file by using __chmod x+a <jar_name>__ in your command line
* On MacOS you must give permission to file by using __chmod 755 <jar_name>__ in your command line

### Maven

You can use Maven to build executable from source code and run it on your machine. 
* Download Maven for your OS [here](https://maven.apache.org/download.cgi)
* Download source code from [Github](https://github.com/dselimovic1/CashRegisterApp)
* Build from source code with __mvn clean package__
* Run JAR with dependencies with __java -jar cash-register-{version}-jar-with-dependencies.jar__

### IntelliJ IDEA

You can run this project in IntelliJ. Steps are as follows:
* Download IntelliJ [here](https://www.jetbrains.com/idea/download)
* Download source code from [Github](https://github.com/dselimovic1/CashRegisterApp)
* Open downloaded project in IntelliJ and open Maven tab (on the right)
* Choose JavaFX plugin __javafx:run__ and run your app

### Linux packages

For now, deb package is available.
* You can download [deb package](https://drive.google.com/file/d/1wdyUIVeVEjyzJrstJqrpTuzjfQKQoQ0k/view?usp=sharing) and install it with __sudo apt install cash-register\_{version}.deb__


## Contributors

* <a href="https://github.com/ebejtovic1" target="_blank">Bejtović Elma</a>
* <a href="https://github.com/Lino2007" target="_blank">Bevanda Lino</a>
* <a href="https://github.com/mand0ne" target="_blank">Mandal Anel</a>
* <a href="https://github.com/nosmanbegovic" target="_blank">Osmanbegović Nur</a>
* <a href="https://github.com/dselimovic1" target="_blank">Selimović Denis</a>
* <a href="https://github.com/silegrb" target="_blank">Šišić Faris</a>
