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
        url: context.ajaxUrl + "filter",
        type: "GET",
        data: $("#mealFilterForm").serialize()
    }).done(updateTableWithData);
}

function clearFilterAndUpdate() {
    $("#mealFilterForm")[0].reset();
    $.get(context.ajaxUrl, updateTableWithData);
}
