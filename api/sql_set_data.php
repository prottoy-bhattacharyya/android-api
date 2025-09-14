<?php
    error_reporting(E_ALL);
    ini_set('display_errors', 1);

    header('Content-Type: application/json');

    $host = "localhost";
    $username = "root";
    $password = "";
    $port = "3306";
    $database = "android_api";

    $response = [];

    try{
        $conn = mysqli_connect($host, $username, $password, $database, $port);
    }
    catch(Exception $e){
        $response = [
            'status' => 'Error',
            'message' => "Connection Failed: " . mysqli_connect_error()
        ];
        echo json_encode($response);
        die();
    }

    if (!isset($_POST["name"]) || !isset($_POST["email"])) {
        $response = [
            'status' => 'Error',
            'message' => 'Required data (name, email) is missing from the POST request.'
        ];
        echo json_encode($response);
        die();
    }

    $name = $_POST["name"];
    $email = $_POST["email"];

    $create_table_query = "CREATE TABLE IF NOT EXISTS table1 (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            name TEXT,
                            email TEXT
                        );";

    if (!mysqli_query($conn, $create_table_query)) {
        $response = [
            'status' => 'Error',
            'message' => "Table Creation Failed: " . mysqli_error($conn)
        ];
    }

    $insert_query = "INSERT INTO table1 (name, email) VALUES (?, ?);";

    $stmt = mysqli_prepare($conn, $insert_query);

    if ($stmt) {
        mysqli_stmt_bind_param($stmt, "ss", $name, $email);

        if (mysqli_stmt_execute($stmt)) {
            $response = [
                'status' => 'Success',
                'message' => 'Data Inserted'
            ];
        } else {
            $response = [
                'status' => 'Error',
                'message' => 'Data Insertion Failed: ' . mysqli_stmt_error($stmt)
            ];
        }
        mysqli_stmt_close($stmt);
    } else {
        $response = [
            'status' => 'Error',
            'message' => 'Statement Preparation Failed: ' . mysqli_error($conn)
        ];
    }

    mysqli_close($conn);

    echo json_encode($response);

?>