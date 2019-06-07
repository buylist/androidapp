package ru.buylist.data;

public class BuyListDbSchema {
    public static final class BuyTable {
        public static final String NAME = "buylist";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
        }
    }

    public static final class ProductTable {
        public static final String NAME = "products";

        public static final class Cols {
            public static final String BUYLIST_ID = "buylist_id";
            public static final String PRODUCT_ID = "product_id";
            public static final String PRODUCT_NAME = "product_name";
            public static final String IS_PURCHASED = "is_purchased";
            public static final String CATEGORY = "category";
            public static final String AMOUNT = "amount";
            public static final String UNIT = "unit";
        }
    }

    public static final class GlobalProductsTable {
        public static final String NAME = "global_list_of_products";

        public static final class Cols {
            public static final String PRODUCT_ID = "product_id";
            public static final String PRODUCT_NAME = "product_name";
            public static final String CATEGORY = "category";
        }
    }
}
