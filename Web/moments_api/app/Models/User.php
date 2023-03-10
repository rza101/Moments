<?php

namespace App\Models;

// use Illuminate\Contracts\Auth\MustVerifyEmail;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;

class User extends Authenticatable
{
    use HasApiTokens, HasFactory, Notifiable;

    /**
     * The attributes that are mass assignable.
     *
     * @var array<int, string>
     */
    protected $table = 'users';
    protected $primaryKey = 'user_id';
    public $incrementing = false;

    function Post(){
        return $this->hasMany(Post::class, "username", "username");
    }
    function Following(){
        return $this->hasMany(UserFollow::class, "username", "username");
    }
    function Follower(){
        return $this->hasMany(UserFollow::class, "username", "username_following");
    }
}
