<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\API\PostsController;
use App\Http\Controllers\API\PostLikesController;
use App\Http\Controllers\API\PostCommentsController;
use App\Http\Controllers\API\SavedPostsController;
use App\Http\Controllers\API\UserController;
use App\Http\Controllers\API\UserFollowController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

// Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
//     return $request->user();
// });

Route::get('posts', [PostsController::class, 'index']);
Route::post('posts', [PostsController::class, 'store']);
Route::get('posts/{id}', [PostsController::class, 'show']);
Route::post('posts/{id}/update', [PostsController::class, 'update']);
Route::post('posts/{id}/delete', [PostsController::class, 'destroy']);

Route::post('postlikes', [PostLikesController::class, 'store']);
Route::get('postlikes/{username}', [PostLikesController::class, 'show']);
Route::post('postlikes/{id}/delete', [PostLikesController::class, 'destroy']);

Route::post('postcomments', [PostCommentsController::class, 'store']);
Route::get('postcomments/{username}', [PostCommentsController::class, 'show']);
Route::post('postcomments/{id}/delete', [PostCommentsController::class, 'destroy']);

Route::post('savedposts', [SavedPostsController::class, 'store']);
Route::get('savedposts/{username}', [SavedPostsController::class, 'show']);
Route::post('savedposts/{id}/delete', [SavedPostsController::class, 'destroy']);

Route::post('users', [UserController::class, 'store']);
Route::get('users/{user_id}', [UserController::class, 'show']);
Route::post('users/{user_id}/update', [UserController::class, 'update']);
// Route::post('users/{user_id}/delete', [UserController::class, 'destroy']);

Route::post('userfollow', [UserFollowController::class, 'store']);
Route::get('userfollow/{username}', [UserFollowController::class, 'show']);
Route::post('userfollow/{id}/delete', [UserFollowController::class, 'destroy']);
