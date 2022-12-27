<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use App\Models\User;
use App\Models\UserFollow;
use Exception;
use Illuminate\Http\Request;
use DB;

class UserFollowController extends Controller
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
            if($request->username == $request->username_following){
                return response()->json([
                    'status' => 400,
                    'message' => 'Failed to store user follow'
                ], 400);
            }
            
            $user_follow = UserFollow::firstOrNew(['username' => $request->username, 'username_following' => $request->username_following]);
            $user_follow->username = $request->username;
            $user_follow->username_following = $request->username_following;
            $user_follow->save();

            return response()->json([
                'status' => 200,
                'message' => 'User follow stored'
            ]);
        } catch (Exception $e) {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to store user follow'
            ], 400);
        }
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($username)
    {
        $user = User::where('username', '=', $username);

        if ($user) {
            $following = DB::select("SELECT user_follow.id, user_follow.username, user_follow.username_following, users.full_name, users.image_url, uf.username as username_following, uf.full_name as full_name_following, uf.image_url as image_url_following
            FROM user_follow 
            INNER JOIN users ON users.username = user_follow.username
            INNER JOIN users uf on uf.username = user_follow.username_following
            Where user_follow.username = :username", ["username" => $request->query($username)]);
            $follower = DB::select("SELECT user_follow.id, user_follow.username, user_follow.username_following, users.full_name, users.image_url, uf.username as username_following, uf.full_name as full_name_following, uf.image_url as image_url_following
            FROM user_follow 
            INNER JOIN users ON users.username = user_follow.username
            INNER JOIN users uf on uf.username = user_follow.username_following
            user_follow.username_following = :username", ["username" => $request->query($username)]);
            return response()->json([
                'status' => 200,
                'message' => 'Success',
                'data' => [
                    'follower' => $follower,
                    'following' => $following
                ]
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to show user follow'
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
    public function update(Request $request, $id)
    {
        return response()->json([
            'status' => 200,
            'message' => 'Success'
        ]);
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        $user_follow = UserFollow::find($id);

        if ($user_follow) {
            $user_follow->delete();

            return response()->json([
                'status' => 200,
                'message' => 'User follow deleted'
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to delete user follow'
            ], 400);
        }
    }
}
