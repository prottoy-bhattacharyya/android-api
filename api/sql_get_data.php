<?php
$host = "localhost";
$username = "root";
$password = "";
$port = "3306";
$db = "table1";

$response = [];

try{
    $conn = mysqli_connect($host, $username, $password, $db, $port);
}
catch(Exception $e){
    $response = [
        'status' => 'failed',
        'message' => "Can't connect to server"
    ];
}

$query = "SELECT * FROM table1";

$result = mysqli_query($conn, $query);

echo result;

?>