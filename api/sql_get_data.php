<?php
$host = "localhost";
$username = "root";
$password = "";
$port = "3306";
$db = "table1";

// Set the content type to application/json for the response
header('Content-Type: application/json');

$response = [];

try {
    $conn = mysqli_connect($host, $username, $password, $db, $port);

    // Check for connection errors
    if (!$conn) {
        throw new Exception("Connection failed: " . mysqli_connect_error());
    }

    $query = "SELECT * FROM table1";
    $result = mysqli_query($conn, $query);

    // Check if the query was successful
    if ($result) {
        $data = [];
        // Loop through the result set and fetch each row as an associative array
        while ($row = mysqli_fetch_assoc($result)) {
            $data[] = $row;
        }

        // Prepare a success response with the fetched data
        $response = [
            'status' => 'success',
            'data' => $data
        ];
    } else {
        // Handle query execution errors
        throw new Exception("Query failed: " . mysqli_error($conn));
    }

    // Close the database connection
    mysqli_close($conn);

} catch (Exception $e) {
    // Catch any exceptions (connection or query errors) and prepare a failed response
    $response = [
        'status' => 'failed',
        'message' => $e->getMessage()
    ];
}

// Encode the response array into a JSON string and echo it
echo json_encode($response);

?>