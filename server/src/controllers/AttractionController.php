<?php

namespace App\Controllers;

use App\Models\Attraction;

/**
 * Class AttractionController
 * Handles attraction-related operations.
 */
class AttractionController
{
    /**
     * Retrieves a list of all attractions and returns them as a JSON response.
     *
     * @return void
     */
    public function getAllAttractions()
    {
        header('Content-Type: application/json');

        try {
            // Fetch attractions from the Attraction model
            $attractions = Attraction::getAll();

            // Return a JSON response with the retrieved data
            echo json_encode([
                'status' => 'success',
                'data' => $attractions
            ]);
        } catch (\Exception $e) {
            // Return a JSON error response in case of an exception
            echo json_encode([
                'status' => 'error',
                'message' => 'Failed to retrieve attractions: ' . $e->getMessage()
            ]);
        }
    }
}
