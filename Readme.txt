To compile the project it is enough to call compile.sh or use ant
To run pseudo distributed on a single machine you may use run.bat

In order to run Distributed for each helper run runHelper.sh <port>
where port is the port that helper is listening for the server

To run the Master run runMaster.sh <configFile>
where config file is a file that has a port that master listens for clients and the specification of the helpers.

To run the client run runClient.sh <masterIP:port>

