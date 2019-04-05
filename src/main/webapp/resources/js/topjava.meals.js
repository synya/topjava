// $(document).ready(function () {
$(function () {
    makeEditable({
            formId: "mealEditForm",
            ajaxUrl: "ajax/profile/meals/",
            update: function () {
                updateTable();
            },
            datatableApi: $("#datatable").DataTable({
                "paging": false,
                "info": true,
                "columns": [
                    {
                        "data": "dateTime"
                    },
                    {
                        "data": "description"
                    },
                    {
                        "data": "calories"
                    },
                    {
                        "defaultContent": "Edit",
                        "orderable": false
                    },
                    {
                        "defaultContent": "Delete",
                        "orderable": false
                    }
                ],
                "order": [
                    [
                        0,
                        "desc"
                    ]
                ]
            })
        }
    );
});

function updateTable() {
    $.ajax({
        url: "ajax/profile/meals/filter",
        type: "GET",
        data: $("#mealFilterForm").serialize()
    }).done(function (data) {
        context.datatableApi.clear().rows.add(data).draw();
    });
}

function clearFilterAndUpdate() {
    $("#mealFilterForm")[0].reset();
    updateTable();
}
