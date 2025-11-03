package com.example.fptstadium.data.model.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetFieldsResponse {

    @SerializedName("data")
    private List<FieldItem> data;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("pageNumber")
    private int pageNumber;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("totalCount")
    private int totalCount;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("hasPreviousPage")
    private boolean hasPreviousPage;

    @SerializedName("hasNextPage")
    private boolean hasNextPage;

    public List<FieldItem> getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    // This class represents a single field item
    public static class FieldItem {
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
        private Integer area;

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
        private Object fieldType;

        // Getters
        public String getId() {
            return id;
        }

        public String getName() {
            return name != null ? name : "Unknown Stadium";
        }

        public String getDescription() {
            return description != null ? description : "";
        }

        public String getAddress() {
            return address != null ? address : "No address";
        }

        public String getFieldTypeId() {
            return fieldTypeId;
        }

        public Double getPricePerHour() {
            return pricePerHour != null ? pricePerHour : 0.0;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public Integer getArea() {
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
            return city != null ? city : "";
        }

        public String getDistrict() {
            return district != null ? district : "";
        }

        // Helper methods for UI
        public String getDistance() {
            // Calculate distance from user location if needed
            // For now return district info
            return district != null && !district.isEmpty() ? "Quận " + district : "N/A";
        }

        public String getHours() {
            // Default hours - can be customized based on fieldType if needed
            return "06:00 - 22:00";
        }

        public String getPhone() {
            // Phone would need to be added to API or fetched from another endpoint
            return "Liên hệ để biết thêm";
        }

        public String getFormattedPrice() {
            if (pricePerHour != null) {
                return String.format("%,.0f VNĐ/giờ", pricePerHour);
            }
            return "Liên hệ";
        }

        public String getFullLocation() {
            StringBuilder location = new StringBuilder();
            if (district != null && !district.isEmpty()) {
                location.append(district).append(", ");
            }
            if (city != null && !city.isEmpty()) {
                location.append(city);
            }
            return location.length() > 0 ? location.toString() : "N/A";
        }
    }
}
