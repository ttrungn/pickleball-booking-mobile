package com.example.fptstadium.data.model.response;

import com.google.gson.annotations.SerializedName;

public class FieldDetailResponse {
    @SerializedName("data")
    private FieldDetail data;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public FieldDetail getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public static class FieldDetail {
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("description")
        private String description;

        @SerializedName("address")
        private String address;

        @SerializedName("fieldTypeId")
        private String fieldTypeId;

        @SerializedName("pricePerHour")
        private Double pricePerHour;

        @SerializedName("imageUrl")
        private String imageUrl;

        @SerializedName("area")
        private Double area;

        @SerializedName("bluePrintImageUrl")
        private String bluePrintImageUrl;

        @SerializedName("latitude")
        private Double latitude;

        @SerializedName("longitude")
        private Double longitude;

        @SerializedName("mapUrl")
        private String mapUrl;

        @SerializedName("city")
        private String city;

        @SerializedName("district")
        private String district;

        @SerializedName("fieldType")
        private FieldType fieldType;

        // Getters
        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getAddress() {
            return address;
        }

        public String getFieldTypeId() {
            return fieldTypeId;
        }

        public Double getPricePerHour() {
            return pricePerHour;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public Double getArea() {
            return area;
        }

        public String getBluePrintImageUrl() {
            return bluePrintImageUrl;
        }

        public Double getLatitude() {
            return latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public String getMapUrl() {
            return mapUrl;
        }

        public String getCity() {
            return city;
        }

        public String getDistrict() {
            return district;
        }

        public FieldType getFieldType() {
            return fieldType;
        }
    }

    public static class FieldType {
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("description")
        private String description;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }
}

