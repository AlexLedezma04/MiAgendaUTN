package orgagendautndatabase

import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class Tasks(
  public val id: Long,
  public val title: String,
  public val description: String,
  public val dueDate: Long,
  public val category: String,
  public val isCompleted: Boolean,
  public val createdAt: Long,
  public val updatedAt: Long,
)
