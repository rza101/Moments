<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\Request;
use Exception;
use PDO;

class UserController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index(Request $request)
    {
        if(!$request->q){
            return response()->json([
                'status' => 400,
                'message' => 'Failed to get user'
            ], 400);
        }

        $user = User::where(function ($query) use ($request) {
            $query->where('full_name', 'like', '%' . $request->q . '%')
                ->orWhere('username', 'like', '%' . $request->q . '%');
        })->get();

        return response()->json([
            'status' => 200,
            'message' => 'Success',
            'data' => $user
        ]);
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        try {
            $user = new User();
            $user->user_id = $request->user_id;
            $user->username = $request->username;
            $user->full_name = $request->full_name;
            // $user->image_url = $request->image_url;
            $user->fcm_token = $request->fcm_token;
            $user->save();

            return response()->json([
                'status' => 200,
                'message' => 'User stored'
            ]);
        } catch (Exception $e) {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to store user'
            ], 400);
        }
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($user_id)
    {
        $user = User::find($user_id);
        if ($user) {
            return response()->json([
                'status' => 200,
                'message' => 'Success',
                'data' => $user
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to show user'
            ], 400);
        }
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $user_id)
    {
        try {
            $user = User::find($user_id);

            if ($user) {
                if ($request->fcm_token) {
                    $user->fcm_token = $request->fcm_token;
                }
                if ($request->full_name) {
                    $user->full_name = $request->full_name;
                }
                if ($request->image_url) {
                    $user->image_url = $request->image_url;
                }
                $user->save();

                return response()->json([
                    'status' => 200,
                    'message' => 'User updated'
                ]);
            } else {
                return response()->json([
                    'status' => 400,
                    'message' => 'Failed to update user'
                ], 400);
            }
        } catch (Exception $e) {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to update user'
            ], 400);
        }
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($user_id)
    {
        $user = User::find($user_id);

        if ($user) {
            $user->delete();

            return response()->json([
                'status' => 200,
                'message' => 'User deleted'
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to delete user'
            ], 400);
        }
    }
}
