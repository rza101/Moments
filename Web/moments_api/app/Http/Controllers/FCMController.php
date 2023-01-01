<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use DB;
use App\Models\User;

class FCMController extends Controller
{
    static function sendFCM($token, $title, $content){
        $url = 'https://fcm.googleapis.com/fcm/send';
        $server_key = 'AAAANl3B6js:APA91bFmnR__EW8OGZttS5XofnDCCfaxbSj1TKX8Od5Ce3wbRmhl-wJ2kAsznuJCG_5fFjoERCXwEIkc0gSMXj77TFe0tB_lX2NIAIxk2hVwAkfayCE2HRVxoaz-_Tnh3focQYmbcF3n';
    
        $fields = array(
            'to' => $token,
            'data' => array(
                'title' => $title,
                'content' => $content
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

        curl_close($ch);

        if ($result === FALSE) {
            die('FCM Send Error: ' . curl_error($ch));
            return false;
        }

        return json_decode($result);
    }

    public function FCM(Request $request)
    {
        $user = User::where('username', '=', $request->username)->first();

        if ($user) {
            $result = $this->sendFCM($user->fcm_token, $request->title, $request->content);

            if($result === false){
                return response()->json([
                    'status' => 400,
                    'message' => 'Failed to send notification'
                ], 400);
            }else{
                if($result->success != 1){
                    return response()->json([
                        'status' => 400,
                        'message' => 'Failed to send notification'
                    ], 400);
                }
                
                return response()->json([
                    'status' => 200,
                    'message' => 'Success',
                    'data' => $result
                ]);
            }
        }else{
            return response()->json([
                'status' => 400,
                'message' => 'Failed to send notification'
            ], 400);
        }
    }
}
