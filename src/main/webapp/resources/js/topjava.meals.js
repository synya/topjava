const mealsAjaxUrl = "ajax/profile/meals/";

function updateFilteredTable() {
    $.ajax({
        type: "GET",
        url: mealsAjaxUrl + "filter",
        data: $("#filter").serialize()
    }).done(updateTableByData);
}

function clearFilter() {
    $("#filter")[0].reset();
    $.get(mealsAjaxUrl, updateTableByData);
}

$(function () {
    makeEditable({
        ajaxUrl: mealsAjaxUrl,
        datatableApi: $("#datatable").DataTable({
            "ajax": {
                "url": mealsAjaxUrl,
                "dataSrc": ""
            },
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime",
                    "render": function (data, type, row) {
                        if (type === "display") {
                            return formatDateTime(data);
                        }
                        return data;
                    }

                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "orderable": false,
                    "defaultContent": "",
                    "render": renderEditBtn
                },
                {
                    "orderable": false,
                    "defaultContent": "",
                    "render": renderDeleteBtn
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ],
            "createdRow": function (row, data, dataIndex) {
                $(row).attr("data-mealExcess", data.excess);
            }
        }),
        updateTable: updateFilteredTable
    });
    $("#startDate").datetimepicker(
        {
            timepicker: false,
            format: 'Y-m-d',
            onShow: function (ct) {
                this.setOptions({
                    maxDate: $("#endDate").val() ? jQuery('#endDate').val() : false
                })
            }
        }
    );
    $("#endDate").datetimepicker(
        {
            timepicker: false,
            format: 'Y-m-d',
            onShow: function (ct) {
                this.setOptions({
                    minDate: $("#startDate").val() ? jQuery('#startDate').val() : false
                })
            }
        }
    );
    $("#startTime").datetimepicker(
        {
            datepicker: false,
            format: 'H:i',
            onShow: function (ct) {
                this.setOptions({
                    maxTime: $("#endTime").val() ? jQuery('#endTime').val() : false
                })
            }
        }
    );
    $("#endTime").datetimepicker(
        {
            datepicker: false,
            format: 'H:i',
            onShow: function (ct) {
                this.setOptions({
                    minTime: $("#startTime").val() ? jQuery('#startTime').val() : false
                })
            }
        }
    );
    $("#dateTime").datetimepicker(
        {
            format: 'Y-m-d H:i'
        }
    );
});