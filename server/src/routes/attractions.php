<?php
use App\Controllers\AttractionController;

// Include necessary files
require_once '../controllers/AttractionController.php';

// Handle GET request for retrieving attractions
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $controller = new AttractionController();
    $controller->getAllAttractions();
}
