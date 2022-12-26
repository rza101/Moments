<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Post extends Model
{
    use HasFactory;

    protected $table = "posts";
    function PostComment(){
        return $this->hasMany(PostComment::class, "id", "post_id");
    }
    function PostLike(){
        return $this->hasMany(PostLike::class, "id", "post_id");
    }
    function SavedPost(){
        return $this->hasMany(SavedPost::class, "id", "post_id");
    }
    function User(){
        return $this->hasOne(User::class, "username", "username");
    }
}
