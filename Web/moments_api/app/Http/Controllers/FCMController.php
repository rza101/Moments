<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use DB;
use App\Models\User;

class FCMController extends Controller
{
    static function sendFCM($token, $title, $body){
        $url = 'https://fcm.googleapis.com/fcm/send';
        $server_key = 'AAAANl3B6js:APA91bFmnR__EW8OGZttS5XofnDCCfaxbSj1TKX8Od5Ce3wbRmhl-wJ2kAsznuJCG_5fFjoERCXwEIkc0gSMXj77TFe0tB_lX2NIAIxk2hVwAkfayCE2HRVxoaz-_Tnh3focQYmbcF3n';
    
        $fields = array(
            'to' => $token,
            'notification' => array(
                'title' => $title,
                'body' => $body
            )
        );
        $fields = json_encode($fields);
    
        $headers = [
            'Authorization: key='.$server_key,
            'Content-Type: application/json'
        ];
    
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $fields);
    
        $result = curl_exec($ch);
        if ($result === FALSE) {
            die('Oops! FCM Send Error: ' . curl_error($ch));
        }
        curl_close($ch);
        if ($result === FALSE) {
            return false;
        }
        return response()->json([
            "status" => $result === true ? true : false,
            "result" => $result
        ]);
    }

    public function FCM(Request $request)
    {
        $token = User::where('username', '=', $request->username)->first();

        $result = $this->sendFCM($token->fcm_token, $request->title, $request->body);
        return $result;
    }
}
