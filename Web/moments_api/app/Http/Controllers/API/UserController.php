<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\Request;
use Exception;

class UserController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        return response()->json([
            'status' => 200,
            'message' => 'Success'
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
            $user->user_id = str_replace(' ','_',$request->user_id);
            $user->full_name = $request->full_name;
            $user->profile_picture = $request->profile_picture;
            $user->save();

            return response()->json([
                'status' => 200,
                'message' => 'User stored',
                'data' => $user
            ]);
        } catch (Exception $e) {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to store user'
            ]);
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
        if($user){
            return response()->json([
                'status' => 200,
                'message' => 'Success',
                'data' => $user
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to show user'
            ]);
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
            $user->full_name = $request->full_name;
            $user->profile_picture = $request->profile_picture;
            $user->save();
            
            return response()->json([
                'status' => 200,
                'message' => 'User updated',
                'data' => $user
            ]);
        } catch (Exception $e) {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to update user'
            ]);
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
            ]);
        }
    }
}
