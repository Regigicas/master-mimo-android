package es.upsa.mimo.datamodule.enums

import es.upsa.mimo.datamodule.R

enum class UsuarioResultEnum
{
    existingUser
    {
        override fun stringValue(): Int
        {
            return R.string.error_existing_username;
        }
    },

    existingEmail
    {
        override fun stringValue(): Int
        {
            return R.string.error_existing_email;
        }

    },

    invalidEmail
    {
        override fun stringValue(): Int
        {
            return R.string.error_invalid_email;
        }
    },

    usernameNotFound
    {
        override fun stringValue(): Int
        {
            return R.string.error_user_not_found;
        }
    },

    passwordMismatch
    {
        override fun stringValue(): Int
        {
            return R.string.error_pass_mismatch;
        }
    },

    ok
    {
        override fun stringValue(): Int
        {
            return R.string.user_insert_ok;
        }
    };

    abstract fun stringValue(): Int;
}
