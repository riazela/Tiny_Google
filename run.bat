del standalone.txt
echo 5000>> standalone.txt

FOR /L %%A IN (1,1,2) DO (
start cmd /k "java -cp bin Helper.Helper 500%%A .
echo 127.0.0.1:500%%A:600%%A>> standalone.txt
)

start cmd /k "java -cp bin Master.Main standalone.txt"
start cmd /k "java -cp bin Master.ClientSample 127.0.0.1:5000"
