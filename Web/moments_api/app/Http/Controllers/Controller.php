<?php

namespace App\Http\Controllers;

use Illuminate\Foundation\Auth\Access\AuthorizesRequests;
use Illuminate\Foundation\Bus\DispatchesJobs;
use Illuminate\Foundation\Validation\ValidatesRequests;
use Illuminate\Routing\Controller as BaseController;

class Controller extends BaseController
{
    use AuthorizesRequests, DispatchesJobs, ValidatesRequests;

    static function sendGCM($token, $title, $body){
        $url = 'https://fcm.googleapis.com/fcm/send';
        $server_key = 'SERVER_KEY_HERE';
    
        $fields = array(
            'to' => $token,
            'notification' => array(
                'title' => $title,
                'body' => $body
            )
        );
        $fields = json_encode($fields);
    
        $headers = array(
            'Authorization: key='.$server_key,
            'Content-Type: application/json'
        );
    
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $fields);
    
        $result = curl_exec($ch);
        curl_close($ch);

        return $result;
    }
}
