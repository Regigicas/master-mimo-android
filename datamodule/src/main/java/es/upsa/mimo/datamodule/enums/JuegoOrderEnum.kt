package es.upsa.mimo.datamodule.enums

import es.upsa.mimo.datamodule.R

enum class JuegoOrderEnum
{
    byDefault
    {
        override fun stringValue(): Int
        {
            return R.string.api_default;
        }

        override fun stringPicker(): Int
        {
            return R.string.order_default;
        }
    },

    byName
    {
        override fun stringValue(): Int
        {
            return R.string.api_name;
        }

        override fun stringPicker(): Int
        {
            return R.string.order_name;
        }
    },

    byNameInverse
    {
        override fun stringValue(): Int
        {
            return R.string.api_name_inverse;
        }

        override fun stringPicker(): Int
        {
            return R.string.order_name_inverse;
        }
    },

    byReleaseDate
    {
        override fun stringValue(): Int
        {
            return R.string.api_released;
        }

        override fun stringPicker(): Int
        {
            return R.string.order_released;
        }
    },

    byReleaseDateInverse
    {
        override fun stringValue(): Int
        {
            return R.string.api_released_inverse;
        }

        override fun stringPicker(): Int
        {
            return R.string.order_released_inverse;
        }
    },

    byAdded
    {
        override fun stringValue(): Int
        {
            return R.string.api_added;
        }

        override fun stringPicker(): Int
        {
            return R.string.order_added;
        }
    },

    byAddedInverse
    {
        override fun stringValue(): Int
        {
            return R.string.api_added_inverse;
        }

        override fun stringPicker(): Int
        {
            return R.string.order_added_inverse;
        }
    },

    byCreated
    {
        override fun stringValue(): Int
        {
            return R.string.api_created;
        }

        override fun stringPicker(): Int
        {
            return R.string.order_created;
        }
    },

    byCreatedInverse
    {
        override fun stringValue(): Int
        {
            return R.string.api_created_inverse;
        }

        override fun stringPicker(): Int
        {
            return R.string.order_created_inverse;
        }
    },

    byRating
    {
        override fun stringValue(): Int
        {
            return R.string.api_rating;
        }

        override fun stringPicker(): Int
        {
            return R.string.order_rating;
        }
    },

    byRatingInverse
    {
        override fun stringValue(): Int
        {
            return R.string.api_rating_inverse;
        }

        override fun stringPicker(): Int
        {
            return R.string.order_rating_inverse;
        }
    };

    abstract fun stringValue(): Int;
    abstract fun stringPicker(): Int;
}
