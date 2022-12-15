<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PostComments extends Model
{
    use HasFactory;

    protected $table = "post_comments";

    protected $fillable = [
        'user_id',
        'post_id',
        'comment'
    ];
}
