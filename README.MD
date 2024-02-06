# SMART HOME

## Influx Setup

### Requirements

- Java 17
- Maven
- [InfluxDB database - version 2.7](https://www.influxdata.com/) listening on port 8086

### Setup

1. Install Java and Maven
2. Install InfluxDB
   - Follow instructions on how to install and start InfluxDB written in [documentation](https://docs.influxdata.com/influxdb/v2/install/) (in case of running commands in terminal, it could require Administrator permissions)
   - When starting InfluxDB, make sure that Antivirus or some firewall does not block accessing application
   - After InfluxDB is started, open in browser `localhost:8086` (default port po InfluxDB is **8086**) and finish the setup procedure that will ask for credentials and other required data for the database; in this example following information are used:
     - username - `client`
     - password - `password`
     - organization - `nwt`
     - bucket - `measurements`
   - InfluxDB will generate access token in one of the step - **save it** since it is required for communication with database
   - Set required information in **application.properties** (`inflxudb.host`, `inflxudb.port`, `inflxudb.organization`, `inflxudb.bucket` and `inflxudb.token`)
3. Load dependencies of Spring Boot application (`pom.xml`) with Maven and start application
4. Open `index.html` in browser (_client_ directory), select measurement and data types and click _Fetch_ button

## Nginx Setup Guide

### Step 1: Download Nginx for Windows

Download the latest version of Nginx for Windows from the official website: [Nginx Downloads](https://nginx.org/en/download.html).

### Step 2: Unzip in Desired Folder

Unzip the downloaded Nginx archive into the desired folder on your Windows machine.

### Step 3: Create Required Directories

- In the root directory, place where you have unzip arhive, create a folder named <b>`data`</b>. Inside the `data` folder, create two subdirectories: <b>`images\profiles`</b>
- In the local disk C create directory <b>`nginx-conf`</b>, then create sub directory and name it <b>`basic-auth`</b>. Inside paste <b>`image-login-htpasswd`</b> file. This is for basic authentication.
  - `user: img-user123`
  - `password: Pass!23`
  - `img-user123:$apr1$N/nzTpl5$SKWBa/VlGGMcrXAuIObGz0` hashed key password pair

### Step 4: Configuration File

Location of configuration: `./conf/nginx.conf`.

### Step 5: Script for Easy Configuration Updates

In the root folder, where `nginx.exe` is present, create a file named `script.bat` This script will automate the process of restarting Nginx every time the configuration file is changed. You can run this script whenever you make changes to the configuration.

**script.bat:**

```batch
@echo off
echo Stopping Nginx...
taskkill /f /im nginx.exe

timeout /t 5 /nobreak >nul

echo Starting Nginx...
cd "C:\path\to\nginx"
start nginx.exe
```

Make sure to replace `"C:\path\to\nginx"` with the actual path to your Nginx executable.

### Step 6: Serving Images

With this setup, every image placed in the `images` directory within the `data` folder will be served by Nginx.

### Additional Notes

- Ensure to run the script (`script.bat`) each time you make changes to the Nginx configuration file (`nginx.conf`).
- Customize the configuration file (`nginx.conf`) based on your specific requirements.

## Run multiple instances of app

1. Open a terminal and navigate to the directory where your `.jar` file is located.

2. Run the instance of the application on wanted port using the following command:

```
java -jar <application>.jar --server.port=<port>
```

3. Repeat process for other instances of app.

## GRAALVM

### Windows

1. Download GraalVM from [https://www.graalvm.org/downloads/](https://www.graalvm.org/downloads/)
2. Move zip into folder where you want to install graalVm
3. Unzip archive
4. Setup path environment GRAALVM_PATH=<path> for example `GRAALVM_PATH=C:\D_PARTICIJA\java-tools\graalvm\oracle21`
5. Restart command prompt
6. Native Image requires Visual Studio and Microsoft Visual C++(MSVC). You can use Visual Studio 2022 version 17.1.0 or later. https://visualstudio.microsoft.com/vs/
7. Check the <b>Desktop development with C++</b> box in the main window. Also, on the right side under Installation Details, choose <b>Windows SDK</b>, and click the Install button.
8. After the installation completes, reboot your system.
9. Native Image sets up build environments automatically if it can find an appropriate Visual Studio installation in a known location. With the GraalVM JDK on your PATH, you can therefore now run the native-image utility in a Command Prompt (cmd) or a PowerShell (pwsh).
10. To check the native-image version, run: `native-image.cmd --version`
11. To build a project using the Native Build Tools Maven plugin, run: `mvnw.cmd native:compile`
12. To create `mvn clean -Pnative native:compile`

## Swagger Documentation

To access the Swagger documentation for this project, open the following URL:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
