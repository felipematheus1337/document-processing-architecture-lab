resource "aws_dynamodb_table" "processing_events" {
  name         = var.dynamodb_table_name
  billing_mode = "PAY_PER_REQUEST"

  hash_key  = "documentId"
  range_key = "eventId"

  attribute {
    name = "documentId"
    type = "S"
  }

  attribute {
    name = "eventId"
    type = "S"
  }

  attribute {
    name = "status"
    type = "S"
  }

  global_secondary_index {
    name            = "status-index"
    hash_key        = "status"
    projection_type = "ALL"
  }

  tags = {
    Name    = var.dynamodb_table_name
    Project = var.project_name
  }
}